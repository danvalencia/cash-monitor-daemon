package com.maquinet.commands.impl

import spock.lang.Specification

/**
 *
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
class SessionCreateCommandTest extends Specification {
    void setup() {

    }

    def "length of Spock's and his friends' names"() {
        expect:
        name.size() == length

        where:
        name     | length
        "Spock"  | 5
        "Kirk"   | 4
        "Scotty" | 6
    }

}
