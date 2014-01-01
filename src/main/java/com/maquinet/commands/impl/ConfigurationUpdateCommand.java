package com.maquinet.commands.impl;

import com.maquinet.commands.Command;
import com.maquinet.events.models.Event;
import com.maquinet.http.HttpService;
import com.maquinet.services.EventService;
import com.maquinet.services.SessionService;

import java.util.logging.Logger;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class ConfigurationUpdateCommand implements Command
{
    private static final Logger LOGGER = Logger.getLogger(ConfigurationUpdateCommand.class.getName());

    private final SessionService sessionService;
    private final HttpService httpService;
    private final EventService eventService;
    private final Event event;

    public ConfigurationUpdateCommand(HttpService httpService, SessionService sessionService, EventService eventService, Event event)
    {
        this.sessionService = sessionService;
        this.httpService = httpService;
        this.eventService = eventService;
        this.event = event;
    }

    @Override
    public void run()
    {
       this.eventService.deleteEvent(event);
    }
}
