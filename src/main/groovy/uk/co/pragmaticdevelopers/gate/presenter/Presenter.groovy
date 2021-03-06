package uk.co.pragmaticdevelopers.gate.presenter

import groovy.json.JsonBuilder
import uk.co.pragmaticdevelopers.gate.AccessRequest
import uk.co.pragmaticdevelopers.gate.AccessToken
import uk.co.pragmaticdevelopers.gate.Client

class Presenter {

    String present(AccessToken token) {
        def builder = new JsonBuilder()
        builder {
            access_token token.value
            token_type 'bearer'
            expires_in token.expiresIn
        }

        builder.toString()
    }

    String present(Client client) {
        JsonBuilder builder = new JsonBuilder()
        builder {
            id client.id
            secret client.secret
        }

        builder.toString()
    }

    String present(AccessRequest accessRequest) {
        def builder = new JsonBuilder(accessRequest)
        builder.toString()
    }
}
