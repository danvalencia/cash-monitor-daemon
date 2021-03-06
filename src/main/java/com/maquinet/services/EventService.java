package com.maquinet.services;

import com.maquinet.events.models.Event;
import com.maquinet.persistence.impl.EventDAO;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
public class EventService
{
    private static final Logger LOGGER = Logger.getLogger(EventService.class.getName());

    private final EventDAO eventDAO;

    public EventService(final EventDAO eventDAO)
    {
        this.eventDAO = eventDAO;
    }

    public Event getFirstEvent()
    {
        return eventDAO.findFirst();
    }

    public List<Event> getAllEvents()
    {
        return eventDAO.findAll();
    }

    public boolean saveAllEvents(final List<Event> events)
    {
        return eventDAO.saveAll(events);
    }

    public boolean saveEvent(final Event event)
    {
        return eventDAO.save(event);
    }

    public Event getEvent(int eventId)
    {
        return eventDAO.get(eventId);
    }

    public boolean deleteEvent(final Event event)
    {
        return eventDAO.delete(event);
    }
}
