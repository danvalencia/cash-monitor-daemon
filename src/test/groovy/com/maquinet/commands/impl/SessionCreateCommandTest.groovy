package com.maquinet.commands.impl

import com.maquinet.events.models.Event
import com.maquinet.events.models.EventType
import com.maquinet.models.Session
import com.maquinet.services.EventService
import com.maquinet.services.HttpService
import com.maquinet.services.SessionService
import com.maquinet.services.impl.CashMonitorHttpService
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.StatusLine
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import spock.lang.Specification

/**
 *
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
class SessionCreateCommandTest extends Specification {
    SessionCreateCommand sessionCreateCommand
    HttpService httpService
    String cashMonitorEndpoint = "http://ec2-54-245-26-209.us-west-2.compute.amazonaws.com"
    Event event

    def sessionService = Mock(SessionService)
    def eventService = Mock(EventService)
    def mockHttpClient = Mock(HttpClient)
    def mockHttpResponse = Mock(CloseableHttpResponse)
    def statusLine = Mock(StatusLine)
    def entity = Mock(HttpEntity)
    def bodyInputStream = new ByteArrayInputStream(new byte[0])

    def setup() {
        httpService = new CashMonitorHttpService(mockHttpClient, cashMonitorEndpoint)

        setup:
        def eventAttributes = ["sesion_creada", "2014-05-10 10:18:51.006"]
        event = EventType.SESSION_CREATE.createEvent(eventAttributes)
        sessionCreateCommand = new SessionCreateCommand(httpService, sessionService, eventService, event)
        sessionService.saveSession(_ as Session) >> true
        mockHttpClient.execute(_ as HttpPost) >> mockHttpResponse
        mockHttpResponse.getStatusLine() >> statusLine
        mockHttpResponse.getEntity() >> entity
        entity.getContent() >> bodyInputStream
    }

    def "should delete event when a 201 response is returned (Created)"() {
        setup:
        statusLine.getStatusCode() >> HttpStatus.SC_CREATED

        when:
        sessionCreateCommand.run()

        then:
        1 * sessionService.deleteCurrentSession()
        1 * eventService.deleteEvent(_ as Event)
        1 * mockHttpResponse.close()
    }

    def "should delete local event when a 404 response is returned (Not Found)"() {
        setup:
        statusLine.getStatusCode() >> HttpStatus.SC_NOT_FOUND

        when:
        sessionCreateCommand.run()

        then:
        1 * sessionService.deleteCurrentSession()
        1 * eventService.deleteEvent(_ as Event)
        1 * mockHttpResponse.close()
    }

    def "should delete local event when a 400 response is returned (Bad Request)"() {
        setup:
        statusLine.getStatusCode() >> HttpStatus.SC_NOT_FOUND

        when:
        sessionCreateCommand.run()

        then:
        1 * sessionService.deleteCurrentSession()
        1 * eventService.deleteEvent(_ as Event)
        1 * mockHttpResponse.close()
    }


    def "should not delete event when there's a 50x response (Internal Server Error)"() {
        setup:
        statusLine.getStatusCode() >> HttpStatus.SC_INTERNAL_SERVER_ERROR

        when:
        sessionCreateCommand.run()

        then:
        1 * sessionService.deleteCurrentSession()
        0 * eventService.deleteEvent(_ as Event)
        1 * mockHttpResponse.close()
    }


}
