package com.maquinet.commands.impl

import com.maquinet.events.models.Event
import com.maquinet.events.models.EventType
import com.maquinet.services.EventService
import com.maquinet.services.HttpService
import com.maquinet.services.SessionService
import com.maquinet.services.impl.CashMonitorHttpService
import org.apache.http.client.HttpClient
import spock.lang.Specification

/**
 *
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
class EmptyCommandTest extends Specification {
    EmptyCommand emptyCommand
    HttpService httpService
    String cashMonitorEndpoint = "http://ec2-54-245-26-209.us-west-2.compute.amazonaws.com"
    Event event

    def eventService = Mock(EventService)
    def sessionService = Mock(SessionService)
    def mockHttpClient = Mock(HttpClient)

    def setup() {
        httpService = new CashMonitorHttpService(mockHttpClient, cashMonitorEndpoint)
        def eventAttributes = ["unimplemented_event", "2014-05-10 10:00:52.723"]
        event = EventType.EMPTY_EVENT.createEvent(eventAttributes)
        emptyCommand = new EmptyCommand(httpService, sessionService, eventService, event)

    }

    def "should delete event always"() {
        when:
        emptyCommand.run()

        then:
        1 * eventService.deleteEvent(_ as Event)
    }
}
