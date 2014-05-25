package com.maquinet.commands.impl;

import com.maquinet.commands.Command;
import com.maquinet.events.models.Event;
import com.maquinet.services.HttpService;
import com.maquinet.models.Session;
import com.maquinet.services.EventService;
import com.maquinet.services.SessionService;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.maquinet.CashMonitorProperties.MACHINE_UUID;

/**
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
public class SessionCloseCommand extends AbstractHttpCommand implements Command
{
    private static final Logger LOGGER = Logger.getLogger(SessionCloseCommand.class.getName());
    private final Session currentSession;


    public SessionCloseCommand(HttpService httpService, SessionService sessionService, EventService eventService, Event event)
    {
        super(httpService, sessionService, eventService, event);
        currentSession = sessionService.getCurrentSession();
    }

    @Override
    public HttpUriRequest buildHttpRequest()
    {
        String endpoint = String.format("%s/api/machines/%s/sessions/%s",
                httpService.getServiceEndpoint(),
                System.getProperty(MACHINE_UUID),
                currentSession.getSessionUuid());

        HttpPut putRequest = new HttpPut(endpoint);
        List<NameValuePair> nameValuePairs = new ArrayList<>();

        // Creation date is used as the end_time, because it refers to this event (SessionCloseEvent), and not
        // to the session.
        nameValuePairs.add(new BasicNameValuePair("end_time", event.formattedEventCreationDate()));
        putRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8));

        LOGGER.info(String.format("Put request is %s", putRequest.toString()));

        return putRequest;
    }

    @Override
    public void handleResponse(HttpResponse httpResponse)
    {
        int statusCode = httpResponse.getStatusLine().getStatusCode();

        if (statusCode == HttpStatus.SC_OK)
        {
            eventService.deleteEvent(event);

            boolean sessionDeleted = sessionService.deleteCurrentSession();
            LOGGER.info(String.format("Current session has been deleted: %s", sessionDeleted));
        }
        else if(statusCode == HttpStatus.SC_NOT_FOUND ||
                statusCode == HttpStatus.SC_BAD_REQUEST)
        {
            // There's not a lot we can do about this, but delete the event to avoid infinite loops.
            eventService.deleteEvent(event);
            LOGGER.info(String.format("Session %s could not be closed. Response is: %s ", currentSession.getSessionUuid(), httpResponse));
        }
        else
        {
            LOGGER.info(String.format("Session %s could not be closed. Response is: %s ", currentSession.getSessionUuid(), httpResponse));
            //Means it's a 500, in which case we retry
        }

    }

    @Override
    public void handleException(HttpResponse httpResponse)
    {

    }

    @Override
    public boolean beforeRequest()
    {
        if (currentSession == null)
        {
            eventService.deleteEvent(event);
            return false;
        }

        return true;
    }

}
