package com.maquinet.commands.impl;

import com.maquinet.commands.Command;
import com.maquinet.events.models.CoinInsertEvent;
import com.maquinet.events.models.Event;
import com.maquinet.services.HttpService;
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
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.maquinet.CashMonitorProperties.*;

/**
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
public class CoinInsertCommand extends AbstractHttpCommand implements Command
{
    private static final Logger LOGGER = Logger.getLogger(CoinInsertCommand.class.getName());

    private Long globalCoinCount;
    private Long newCoinCount;
    private final Session currentSession;

    public CoinInsertCommand(HttpService httpService, SessionService sessionService, EventService eventService, Event event)
    {
        super(httpService, sessionService, eventService, event);
        this.currentSession = this.sessionService.getCurrentSession();
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
        long coinCount = currentSession.getCoinCount() + 1;
        nameValuePairs.add(new BasicNameValuePair("coin_count", Long.toString(coinCount)));
        nameValuePairs.add(new BasicNameValuePair("global_coin_count", globalCoinCount.toString()));

        putRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8));

        LOGGER.info(String.format("Put request is %s", putRequest.toString()));

        return putRequest;
    }

    @Override
    public void handleResponse(HttpResponse httpResponse)
    {
        int statusCode = httpResponse.getStatusLine().getStatusCode();

        if(statusCode == HttpStatus.SC_OK)
        {
            eventService.deleteEvent(event);
            // Saving the current session to disk to update the coin count
            currentSession.setCoinCount(newCoinCount);
            boolean sessionSaved = sessionService.saveSession(currentSession);
            LOGGER.info(String.format("After updating coin count sesssion has been saved: %s", sessionSaved));
        }
        else if(statusCode == HttpStatus.SC_NOT_FOUND ||
                statusCode == HttpStatus.SC_BAD_REQUEST)
        {
            // There's not a lot we can do about this, but delete the event to avoid infinite loops.
            eventService.deleteEvent(event);
            LOGGER.info(String.format("Session %s could not be updated. Response is: %s ", currentSession.getSessionUuid(), httpResponse));
        }
        else
        {
            //Means it's a 500, in which case we retry
            LOGGER.info(String.format("Session %s could not be updated. Response is: %s ", currentSession.getSessionUuid(), httpResponse));
        }

    }

    @Override
    public void handleException(HttpResponse httpResponse)
    {

    }

    @Override
    public boolean beforeRequest()
    {
        if(currentSession == null)
        {
            eventService.deleteEvent(event);
            return false;
        }

        globalCoinCount = ((CoinInsertEvent) event).getGlobalCoinCount();
        newCoinCount = currentSession.getCoinCount() + 1;

        return true;
    }

}
