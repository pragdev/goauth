package goauth

import groovy.transform.Canonical

import java.lang.Long as Seconds

@Canonical
class AccessToken extends Token {
    String value
    Date issuedOn
    Seconds expiresIn

    AccessToken(Map properties) {
        this(properties.issuedOn, properties.expiresIn)
        if(properties.value) this.value = properties.value
    }

    AccessToken(Date issuedOn, Seconds expiresIn) {
        this.value = UUID.randomUUID().toString()
        this.issuedOn = issuedOn
        this.expiresIn = expiresIn
    }

    AccessToken() {
        this(new Date(), 3600)
    }

    boolean isExpired() {
        def now = new Date()
        now.after expiryDate()
    }

    private Date expiryDate() {
        new Date(issuedOn.time + expiresIn * 1000)
    }

    String toString() {
        value
    }

    @Override
    def describe() {
        ['access_token', value]
    }
}
