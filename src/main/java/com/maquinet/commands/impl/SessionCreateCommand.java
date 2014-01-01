package com.maquinet.commands.impl;

import com.maquinet.commands.Command;
import com.maquinet.http.HttpService;
import com.maquinet.models.Event;
import com.maquinet.models.Session;
import com.maquinet.services.EventService;
import com.maquinet.services.SessionService;
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
import java.util.logging.Logger;

import static com.maquinet.CashMonitorProperties.*;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class SessionCreateCommand implements Command
{
    private static final Logger LOGGER = Logger.getLogger("SessionCreateCommand");

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
        LOGGER.info(String.format("Before deleting current session"));
        sessionService.deleteCurrentSession();
        UUID sessionUUID = UUID.randomUUID();
        LOGGER.info(String.format("Running session create command for session %s", sessionUUID));
        Session session = new Session(sessionUUID.toString());
        if(sessionService.saveSession(session))
        {
            LOGGER.info(String.format("Session %s has been saved", sessionUUID));
            HttpClient httpClient = httpService.getHttpClient();
            LOGGER.info(String.format("HttpClient is %s", httpClient));
            String endpoint = String.format("%s/api/machines/%s/sessions/%s",
                                                httpService.getServiceEndpoint(),
                                                System.getProperty(MACHINE_UUID),
                                                sessionUUID.toString());
            LOGGER.info(String.format("Service Endpoint is %s", endpoint));
            HttpPost post = new HttpPost(endpoint);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("start_time", event.formattedCreationDate()));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8));
            LOGGER.info(String.format("Post request is %s", post.toString()));
            HttpResponse response = null;
            try
            {
                LOGGER.info(String.format("Before executing post request"));
                response = httpClient.execute(post);
                LOGGER.info(String.format("After executing post request.  Status code: %s", response.getStatusLine().getStatusCode()));
                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED)
                {
                    boolean eventDeleted = eventService.deleteEvent(event);
                    LOGGER.info(String.format("Session %s created successfully", sessionUUID.toString()));
                    LOGGER.info(String.format("Event was deleted: %s", eventDeleted));
                }
            } catch (IOException e)
            {
                LOGGER.severe(String.format("Exception making http request to %s", post.toString()));
                e.printStackTrace();
                //retry ?
            } finally
            {
                if(response != null && response instanceof CloseableHttpResponse)
                {
                    try
                    {
                        ((CloseableHttpResponse) response).close();
                    } catch (IOException e)
                    {
                        LOGGER.severe(String.format("Error trying to close response for session %s", sessionUUID.toString()));
                    }
                }
            }
        }
    }
}
