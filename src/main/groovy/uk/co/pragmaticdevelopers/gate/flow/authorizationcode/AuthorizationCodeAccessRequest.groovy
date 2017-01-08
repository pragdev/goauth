package uk.co.pragmaticdevelopers.gate.flow.authorizationcode

import groovy.transform.Canonical
import uk.co.pragmaticdevelopers.gate.AccessRequest
import uk.co.pragmaticdevelopers.gate.AuthorizationCode

@Canonical
class AuthorizationCodeAccessRequest extends AccessRequest {

    @Override
    def makeToken() {
        return new AuthorizationCode()
    }
}