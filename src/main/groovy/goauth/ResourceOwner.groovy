package goauth

import groovy.transform.Canonical
import groovy.transform.TupleConstructor

@Canonical
@TupleConstructor
class ResourceOwner {
    String username
    String password
    String displayName

    boolean accept(Credentials credentials) {
        username == credentials.username && password == credentials.password
    }

    Credentials getCredentials() {
        return new Credentials(username: username, password: password)
    }
}
