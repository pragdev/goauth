package goauth

import groovy.util.logging.Log

import static goauth.AccessRequest.Status.DENIED
import static goauth.AccessRequest.Status.GRANTED
import static goauth.AuthenticationFlow.PASSWORD

enum AuthenticationFlow {
    CLIENT_CREDENTIALS, PASSWORD, REFRESH_TOKEN, AUTHORIZATION_CODE
}

@Log
class Security {
    ResourceOwnerRepository resourceOwnerRepository
    ClientRepository clientsRepository
    AccessRequestRepository accessRequestRepository
    AccessRequestFactory accessRequestFactory
    TokenRepository tokenRepository

    AccessToken authenticateResourceOwner(Credentials credentials) throws InvalidCredentialsException {
        if (!resourceOwnerRepository.exists(credentials.username)) throw new InvalidCredentialsException(credentials)

        ResourceOwner storedResourceOwner = resourceOwnerRepository.findBy credentials.username
        if (!storedResourceOwner?.accept(credentials)) throw new InvalidCredentialsException(credentials)

        tokenRepository.store new AccessToken()
    }

    AccessToken authenticateClient(Credentials credentials) throws InvalidCredentialsException {
        if (!clientsRepository.exists(credentials.username)) throw new InvalidCredentialsException(credentials)

        Client storedClient = clientsRepository.findBy(credentials.username)
        if (!storedClient.accept(credentials)) throw new InvalidCredentialsException(credentials)

        tokenRepository.store new AccessToken()
    }

    Client register(Client client) {
        client.id = UUID.randomUUID().toString()
        client.secret = UUID.randomUUID().toString()

        clientsRepository.store client
    }

    Client findClientBy(id) {
        clientsRepository.findBy id
    }

    ResourceOwner identifyResourceOwnerBy(Credentials credentials) {
        if (!credentials || credentials.incomplete) throw new InvalidCredentialsException(credentials)

        ResourceOwner storedOwner = resourceOwnerRepository.findBy credentials.username
        if (!storedOwner?.accept(credentials)) throw new InvalidCredentialsException(credentials)

        return storedOwner
    }

    AccessRequest makeAccessRequestFor(Client client, ResourceOwner resourceOwner, GrantRequest grantRequest) {
        // TODO test store is called
        AccessRequest accessRequest = accessRequestFactory.make(client: client, resourceOwner: resourceOwner, grantRequest: grantRequest)
        accessRequestRepository.store accessRequest
    }

    Token grantAccess(AccessRequest accessRequest) {
        if (!(accessRequest.status in [GRANTED, DENIED])) throw new InvalidStatusException()
        if (!accessRequestRepository.exists(accessRequest.id)) throw new EntityNotFound()

        def storedAccessRequest = accessRequestRepository.findBy accessRequest.id
        storedAccessRequest.status = GRANTED
        accessRequestRepository.store storedAccessRequest
        storedAccessRequest.makeToken()
    }

    URI redirectUriFor(AccessRequest accessRequest) {
        def request = accessRequestRepository.findBy accessRequest.id
        request.client.redirectionUri
    }

    AccessRequest issueAccessRequest(GrantRequest grantRequest, Credentials credentials) {
        if(!grantRequest.validType) throw new InvalidResponseTypeException()

        Client client = findClientBy grantRequest.clientId
        if (!client) throw new EntityNotFound()

        def owner = identifyResourceOwnerBy credentials

        makeAccessRequestFor(client, owner, grantRequest)
    }

    AccessToken issueAccessToken(AccessTokenPasswordFlowRequest tokenRequest, Credentials credentials) {
        AuthenticationFlow flow = tokenRequest.grantType.toUpperCase()
        if(flow != PASSWORD) throw new InvalidGrantTypeException()

        def token = authenticateResourceOwner credentials

        return token
    }
}
