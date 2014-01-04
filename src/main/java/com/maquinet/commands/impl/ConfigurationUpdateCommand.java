package com.maquinet.commands.impl;

import com.maquinet.commands.Command;
import com.maquinet.events.models.ConfigurationUpdateEvent;
import com.maquinet.events.models.Event;
import com.maquinet.services.HttpService;
import com.maquinet.services.EventService;
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

import static com.maquinet.CashMonitorProperties.MACHINE_UUID;

/**
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
public class ConfigurationUpdateCommand implements Command
{
    private static final Logger LOGGER = Logger.getLogger(ConfigurationUpdateCommand.class.getName());

    private final HttpService httpService;
    private final EventService eventService;
    private final Event event;

    public ConfigurationUpdateCommand(HttpService httpService, EventService eventService, Event event)
    {
        this.httpService = httpService;
        this.eventService = eventService;
        this.event = event;
    }

    @Override
    public void run()
    {
        HttpClient httpClient = httpService.getHttpClient();

        String endpoint = String.format("%s/api/machines/%s",
                httpService.getServiceEndpoint(),
                System.getProperty(MACHINE_UUID));

        HttpPut putRequest = new HttpPut(endpoint);

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        String coinTime = ((ConfigurationUpdateEvent) event).getCoinTime().toString();
        String coinValue = ((ConfigurationUpdateEvent) event).getCoinValue().toString();

        nameValuePairs.add(new BasicNameValuePair("coin_time", coinTime));
        nameValuePairs.add(new BasicNameValuePair("coin_value", coinValue));

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
            }
            else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR)
            {
                //ToDo: Print response body
                LOGGER.severe(String.format("Internal server error trying to update server configuration"));
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

    private void deleteEvent()
    {
        boolean eventDeleted = eventService.deleteEvent(event);
        LOGGER.info(String.format("Configuration Update Event was deleted: %s", eventDeleted));
    }

}
