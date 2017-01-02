package goauth

import com.google.appengine.api.datastore.*
import groovy.transform.TupleConstructor

import static com.google.appengine.api.datastore.Query.FilterOperator.IN

@TupleConstructor
class AccessRequestRepository {
    DatastoreService datastore = DatastoreServiceFactory.datastoreService
    ResourceOwnerRepository resourceOwnerRepository
    ClientRepository clientsRepository
    AccessRequestFactory accessRequestFactory

    AccessRequest store(AccessRequest accessRequest) {
        if (!accessRequest.id) throw new IllegalArgumentException('An accessRequest id is required')

        Entity entity = Entity.make(accessRequest.id, AccessRequest)
        entity['id'] = accessRequest.id
        entity['status'] = accessRequest.status.name()
        entity['client'] = accessRequest.client?.id
        entity['resourceOwner'] = accessRequest.resourceOwner?.username
        entity['type'] = accessRequest.class.simpleName

        datastore.put entity
        accessRequest
    }

    private Key asKey(String param, String kind) {
        def id = URLEncoder.encode(param, 'UTF-8')
        KeyFactory.createKey(kind, id)
    }

    boolean exists(String id) {
        findAccessRequestBy id
    }

    AccessRequest findBy(String id) {
        def entity = findAccessRequestBy id
        if (!entity) return null

        def clientId = entity.getProperty('client').toString()
        def name = entity.getProperty('resourceOwner').toString()
        def resourceOwner = resourceOwnerRepository.findBy name
        def client = clientsRepository.findBy clientId

        accessRequestFactory.make(entity, client, resourceOwner)
    }

    private Entity findAccessRequestBy(String id) {
        def query = datastore.prepare new Query(AccessRequest.simpleName).setFilter(new Query.FilterPredicate('id', IN, [id]))
        query.asSingleEntity() ?: null
    }

}
