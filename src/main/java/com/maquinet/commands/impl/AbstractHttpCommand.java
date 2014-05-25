package com.maquinet.commands.impl;

import com.maquinet.commands.Command;
import com.maquinet.events.models.Event;
import com.maquinet.services.EventService;
import com.maquinet.services.HttpService;
import com.maquinet.services.SessionService;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public abstract class AbstractHttpCommand implements Command
{
    protected final SessionService sessionService;
    protected final HttpService httpService;
    protected final EventService eventService;
    protected final Event event;

    public AbstractHttpCommand(HttpService httpService, SessionService sessionService, EventService eventService, Event event)
    {
        this.sessionService = sessionService;
        this.httpService = httpService;
        this.eventService = eventService;
        this.event = event;
    }

    @Override
    public void run()
    {
        boolean shouldPerformRequest = beforeRequest();
        if(shouldPerformRequest)
        {
            HttpClient httpClient = httpService.getHttpClient();
            HttpUriRequest request = buildHttpRequest();
            HttpResponse response = null;
            try
            {
                response = httpClient.execute(request);
                handleResponse(response);
            } catch (IOException e)
            {
                handleException(response);
            } finally
            {
                if(response != null && response instanceof CloseableHttpResponse)
                {
                    try
                    {
                        ((CloseableHttpResponse) response).close();
                    }
                    catch (IOException e)
                    {
//                        LOGGER.severe(String.format("Error trying to close response for request %s", request.toString()));
                    }
                }
            }
        }
    }

    public abstract HttpUriRequest buildHttpRequest();
    public abstract void handleResponse(HttpResponse httpResponse);
    public abstract void handleException(HttpResponse httpResponse);
    public abstract boolean beforeRequest();
}
