package com.maquinet.commands.impl;

import com.maquinet.commands.Command;
import com.maquinet.services.HttpService;
import com.maquinet.events.models.Event;
import com.maquinet.models.Session;
import com.maquinet.services.EventService;
import com.maquinet.services.SessionService;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static com.maquinet.CashMonitorProperties.*;

/**
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
public class SessionCreateCommand extends AbstractHttpCommand implements Command
{
    private static final Logger LOGGER = Logger.getLogger(SessionCreateCommand.class.getName());

    private final SessionService sessionService;
    private final HttpService httpService;
    private final EventService eventService;
    private final Event event;
    private UUID sessionUUID;

    public SessionCreateCommand(HttpService httpService, SessionService sessionService, EventService eventService, Event event)
    {
        super(httpService, sessionService, eventService, event);

        this.sessionService = sessionService;
        this.httpService = httpService;
        this.eventService = eventService;
        this.event = event;
    }

    @Override
    public HttpUriRequest buildHttpRequest()
    {
        String endpoint = String.format("%s/api/machines/%s/sessions/%s",
                                            httpService.getServiceEndpoint(),
                                            System.getProperty(MACHINE_UUID),
                                            sessionUUID.toString());
        LOGGER.info(String.format("Service Endpoint is %s", endpoint));
        HttpPost postRequest = new HttpPost(endpoint);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("start_time", event.formattedEventCreationDate()));
        postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8));

        return postRequest;
    }

    @Override
    public void handleResponse(HttpResponse httpResponse)
    {
        int statusCode = httpResponse.getStatusLine().getStatusCode();

        if(statusCode == HttpStatus.SC_CREATED)
        {
            LOGGER.info(String.format("Session %s created successfully", sessionUUID.toString()));
            eventService.deleteEvent(event);
        }
        else if(statusCode == HttpStatus.SC_NOT_FOUND ||
                                statusCode == HttpStatus.SC_BAD_REQUEST)
        {
            LOGGER.info(String.format("Session %s could not be created. Response is: %s ", sessionUUID.toString(), httpResponse));
            eventService.deleteEvent(event);
        }
    }

    @Override
    public void handleException(HttpResponse httpResponse)
    {

    }

    @Override
    public boolean beforeRequest()
    {
        sessionService.deleteCurrentSession();
        sessionUUID = UUID.randomUUID();
        Session currentSession = new Session(sessionUUID.toString());
        boolean sessionSaved = sessionService.saveSession(currentSession);
        LOGGER.info(String.format("Session %s has been saved: %s", sessionUUID, sessionSaved));

        return sessionSaved;
    }

}
