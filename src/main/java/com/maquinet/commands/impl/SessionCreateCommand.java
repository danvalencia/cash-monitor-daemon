package com.maquinet.commands.impl;

import com.maquinet.commands.Command;
import com.maquinet.services.HttpService;
import com.maquinet.events.models.Event;
import com.maquinet.models.Session;
import com.maquinet.services.EventService;
import com.maquinet.services.SessionService;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.maquinet.CashMonitorProperties.*;

/**
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
public class SessionCreateCommand implements Command
{
    private static final Logger LOGGER = Logger.getLogger(SessionCreateCommand.class.getName());

    private final SessionService sessionService;
    private final HttpService httpService;
    private final EventService eventService;
    private final Event event;

    public SessionCreateCommand(HttpService httpService, SessionService sessionService, EventService eventService, Event event)
    {
        this.sessionService = sessionService;
        this.httpService = httpService;
        this.eventService = eventService;
        this.event = event;
    }

    @Override
    public void run()
    {
        sessionService.deleteCurrentSession();
        UUID sessionUUID = UUID.randomUUID();
        Session currentSession = new Session(sessionUUID.toString());
        if(sessionService.saveSession(currentSession))
        {
            LOGGER.info(String.format("Session %s has been saved", sessionUUID));
            HttpClient httpClient = httpService.getHttpClient();
            String endpoint = String.format("%s/api/machines/%s/sessions/%s",
                                                httpService.getServiceEndpoint(),
                                                System.getProperty(MACHINE_UUID),
                                                sessionUUID.toString());
            LOGGER.info(String.format("Service Endpoint is %s", endpoint));
            HttpPost postRequest = new HttpPost(endpoint);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("start_time", event.formattedCreationDate()));
            postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8));
            LOGGER.info(String.format("Post request is %s", postRequest.toString()));
            HttpResponse response = null;
            try
            {
                LOGGER.info(String.format("Before executing post request"));
                response = httpClient.execute(postRequest);
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = IOUtils.toString(response.getEntity().getContent());

                if(statusCode == HttpStatus.SC_CREATED)
                {
                    LOGGER.info(String.format("Session %s created successfully", sessionUUID.toString()));
                    deleteEvent();
                }
                else if(statusCode == HttpStatus.SC_NOT_FOUND ||
                        statusCode == HttpStatus.SC_BAD_REQUEST)
                {
                    // There's not a lot we can do about this, but delete the event to avoid infinite loops.
                    deleteEvent();
                    LOGGER.info(String.format("Session %s could not be created. Response is: %s ", currentSession.getSessionUuid(), responseBody));
                }
                else
                {
                    //Means it's a 500, in which case we retry
                    LOGGER.info(String.format("Session %s could not be created. Response is: %s ", currentSession.getSessionUuid(), responseBody));
                }

            } catch (IOException e)
            {
                LOGGER.log(Level.SEVERE, String.format("Exception making http request to %s", postRequest.toString()), e);
            } finally
            {
                if(response != null && response instanceof CloseableHttpResponse)
                {
                    try
                    {
                        ((CloseableHttpResponse) response).close();
                    } catch (IOException e)
                    {
                        LOGGER.log(Level.SEVERE, String.format("Error trying to close response for session %s", sessionUUID.toString()), e);
                    }
                }
            }
        }
    }

    private void deleteEvent()
    {
        boolean eventDeleted = eventService.deleteEvent(event);
        LOGGER.info(String.format("Event was deleted: %s", eventDeleted));
    }
}
