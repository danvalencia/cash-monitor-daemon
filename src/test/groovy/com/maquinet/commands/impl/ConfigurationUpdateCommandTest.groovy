package com.maquinet.commands.impl

import com.maquinet.events.models.Event
import com.maquinet.events.models.EventType
import com.maquinet.services.EventService
import com.maquinet.services.HttpService
import com.maquinet.services.impl.CashMonitorHttpService
import org.apache.http.HttpEntity
import org.apache.http.HttpStatus
import org.apache.http.StatusLine
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPut
import spock.lang.Specification

/**
 *
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
class ConfigurationUpdateCommandTest extends Specification {
    ConfigurationUpdateCommand configurationUpdateCommand
    HttpService httpService
    String cashMonitorEndpoint = "http://ec2-54-245-26-209.us-west-2.compute.amazonaws.com"
    Event event

    def eventService = Mock(EventService)
    def mockHttpClient = Mock(HttpClient)
    def mockHttpResponse = Mock(CloseableHttpResponse)
    def statusLine = Mock(StatusLine)
    def entity = Mock(HttpEntity)
    def bodyInputStream = new ByteArrayInputStream(new byte[0])

    def setup() {
        httpService = new CashMonitorHttpService(mockHttpClient, cashMonitorEndpoint)
        def eventAttributes = ["configuracion_actualizada", "2014-05-10 10:00:52.723", "5", "5"]
        event = EventType.CONFIG_UPDATE.createEvent(eventAttributes)
        configurationUpdateCommand = new ConfigurationUpdateCommand(httpService, eventService, event)

        setup:
        mockHttpClient.execute(_ as HttpPut) >> mockHttpResponse
        mockHttpResponse.getStatusLine() >> statusLine
        mockHttpResponse.getEntity() >> entity
        entity.getContent() >> bodyInputStream
    }

    def "should delete event when a session exists and response is 200 (OK)"() {
        setup:
        statusLine.getStatusCode() >> HttpStatus.SC_OK

        when:
        configurationUpdateCommand.run()

        then:
        1 * eventService.deleteEvent(_ as Event)
        1 * mockHttpResponse.close()
    }

    // Verificar esta prueba, ya que la funcionalidad pueque no sea la correcta,
    // Creo que tenemos que borrar la sesión actual
    def "should delete local event when a 404 response is returned (Not Found)"() {
        setup:
        statusLine.getStatusCode() >> HttpStatus.SC_NOT_FOUND

        when:
        configurationUpdateCommand.run()

        then:
        1 * eventService.deleteEvent(_ as Event)
        1 * mockHttpResponse.close()
    }

    // Verificar esta prueba también, por las mismas razones que la anterior
    def "should delete local event when a 400 response is returned (Bad Request)"() {
        setup:
        statusLine.getStatusCode() >> HttpStatus.SC_BAD_REQUEST

        when:
        configurationUpdateCommand.run()

        then:
        1 * eventService.deleteEvent(_ as Event)
        1 * mockHttpResponse.close()
    }


    def "If we get a 50x response (Internal Server Error), we retry instead; we don't delete event."() {
        setup:
        statusLine.getStatusCode() >> HttpStatus.SC_INTERNAL_SERVER_ERROR

        when:
        configurationUpdateCommand.run()

        then:
        0 * eventService.deleteEvent(_ as Event)
        1 * mockHttpResponse.close()
    }
}
