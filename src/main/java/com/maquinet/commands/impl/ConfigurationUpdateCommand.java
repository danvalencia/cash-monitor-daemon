package com.maquinet.commands.impl;

import com.maquinet.commands.Command;
import com.maquinet.events.models.ConfigurationUpdateEvent;
import com.maquinet.events.models.Event;
import com.maquinet.services.EventService;
import com.maquinet.services.HttpService;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
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
public class ConfigurationUpdateCommand extends AbstractHttpCommand implements Command
{
    private static final Logger LOGGER = Logger.getLogger(ConfigurationUpdateCommand.class.getName());

    public ConfigurationUpdateCommand(HttpService httpService, EventService eventService, Event event)
    {
        super(httpService, null, eventService, event);
    }

    @Override
    public HttpUriRequest buildHttpRequest()
    {
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

        return putRequest;
    }

    @Override
    public void handleResponse(HttpResponse response)
    {
        int statusCode = response.getStatusLine().getStatusCode();

        String responseBody = extractResponseBody(response);
        LOGGER.info(String.format("After executing put request.  Status code: %s", statusCode));

        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        {
            deleteEvent();
        }
        else if(statusCode == HttpStatus.SC_NOT_FOUND ||
                statusCode == HttpStatus.SC_BAD_REQUEST)
        {
            // There's not a lot we can do about this, but delete the event to avoid infinite loops.
            deleteEvent();
            LOGGER.info(String.format("Configuration for machine could not be updated. Response is: %s ", responseBody));
        }
        else
        {
            //Means it's a 500, in which case we retry
            LOGGER.info(String.format("Configuration for machine could not be updated. Response is: %s ", responseBody));
        }


    }

    private String extractResponseBody(HttpResponse response)
    {
        try
        {
            return IOUtils.toString(response.getEntity().getContent());
        } catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, String.format("Exception trying to read response for %s", response), e);
        }
        return "";
    }

    @Override
    public void handleException(HttpResponse httpResponse, Exception exception)
    {
        LOGGER.log(Level.SEVERE, String.format("Exception making http request to %s", httpResponse), exception);

    }

    @Override
    public boolean beforeRequest()
    {
        return true;
    }

    private void deleteEvent()
    {
        boolean eventDeleted = eventService.deleteEvent(event);
        LOGGER.info(String.format("Configuration Update Event was deleted: %s", eventDeleted));
    }

}
