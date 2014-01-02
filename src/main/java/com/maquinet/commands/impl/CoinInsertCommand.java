package com.maquinet.commands.impl;

import com.maquinet.commands.Command;
import com.maquinet.events.models.CoinInsertEvent;
import com.maquinet.events.models.Event;
import com.maquinet.services.HttpService;
import com.maquinet.models.Session;
import com.maquinet.services.EventService;
import com.maquinet.services.SessionService;
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
 * @author Daniel Valencia (daniel@tacitknowledge.com)
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
            currentSession.incrementCoinCount();

            HttpClient httpClient = httpService.getHttpClient();

            String endpoint = String.format("%s/api/machines/%s/sessions/%s",
                    httpService.getServiceEndpoint(),
                    System.getProperty(MACHINE_UUID),
                    currentSession.getSessionUuid());

            HttpPut putRequest = new HttpPut(endpoint);

            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("coin_count", currentSession.getCoinCount().toString()));
            nameValuePairs.add(new BasicNameValuePair("global_coin_count", globalCoinCount.toString()));

            putRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8));

            LOGGER.info(String.format("Put request is %s", putRequest.toString()));
            HttpResponse response = null;
            try
            {
                response = httpClient.execute(putRequest);
                LOGGER.info(String.format("After executing put request.  Status code: %s", response.getStatusLine().getStatusCode()));

                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                {
                    deleteEvent();
                    // Saving the current session to disk to update the coin count
                    boolean sessionSaved = sessionService.saveSession(currentSession);
                    LOGGER.info(String.format("After updating coin count sesssion has been saved: %s", sessionSaved));
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
