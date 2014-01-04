package com.maquinet.commands.impl;

import com.maquinet.commands.Command;
import com.maquinet.events.models.Event;
import com.maquinet.services.HttpService;
import com.maquinet.services.EventService;
import com.maquinet.services.SessionService;

import java.util.logging.Logger;

/**
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
public class CounterResetCommand implements Command
{
    private static final Logger LOGGER = Logger.getLogger(CounterResetCommand.class.getName());

    private final SessionService sessionService;
    private final HttpService httpService;
    private final EventService eventService;
    private final Event event;

    public CounterResetCommand(HttpService httpService, SessionService sessionService, EventService eventService, Event event)
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
