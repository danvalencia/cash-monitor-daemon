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
public class CoinInsertCommand implements Command
{
    private static final Logger LOGGER = Logger.getLogger(CoinInsertCommand.class.getName());

    private final SessionService sessionService;
    private final HttpService httpService;
    private final EventService eventService;
    private final Event event;

    public CoinInsertCommand(HttpService httpService, SessionService sessionService, EventService eventService, Event event)
    {
        this.sessionService = sessionService;
        this.httpService = httpService;
        this.eventService = eventService;
        this.event = event;
    }

    @Override
    public void run()
    {
        Long globalCoinCount = ((CoinInsertEvent) event).getGlobalCoinCount();
        Session currentSession = sessionService.getCurrentSession();
        if(currentSession == null)
        {
            deleteEvent();
        }
        else
        {
            HttpClient httpClient = httpService.getHttpClient();

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
            HttpResponse response = null;
            try
            {
                response = httpClient.execute(putRequest);
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = IOUtils.toString(response.getEntity().getContent());

                LOGGER.info(String.format("After executing put request.  Status code: %s", statusCode));

                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                {
                    deleteEvent();
                    // Saving the current session to disk to update the coin count
                    currentSession.setCoinCount(coinCount);
                    boolean sessionSaved = sessionService.saveSession(currentSession);
                    LOGGER.info(String.format("After updating coin count sesssion has been saved: %s", sessionSaved));
                }
                else if(statusCode == HttpStatus.SC_NOT_FOUND ||
                        statusCode == HttpStatus.SC_BAD_REQUEST)
                {
                    // There's not a lot we can do about this, but delete the event to avoid infinite loops.
                    deleteEvent();
                    LOGGER.info(String.format("Session %s could not be updated. Response is: %s ", currentSession.getSessionUuid(), responseBody));
                }
                else
                {
                    //Means it's a 500, in which case we retry
                    LOGGER.info(String.format("Session %s could not be updated. Response is: %s ", currentSession.getSessionUuid(), responseBody));
                }

            }
            catch (IOException e)
            {
                LOGGER.log(Level.SEVERE, String.format("Exception making http request to %s", putRequest.toString()), e);
            }
            finally
            {
                if(response != null && response instanceof CloseableHttpResponse)
                {
                    try
                    {
                        ((CloseableHttpResponse) response).close();
                    }
                    catch (IOException e)
                    {
                        LOGGER.severe(String.format("Error trying to close response for request %s", putRequest.toString()));
                    }
                }
            }
        }
    }

    private void deleteEvent()
    {
        boolean eventDeleted = eventService.deleteEvent(event);
        LOGGER.info(String.format("Coin Insert Event was deleted: %s", eventDeleted));
    }
}
