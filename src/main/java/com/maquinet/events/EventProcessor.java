package com.maquinet.events;

import com.maquinet.events.models.Event;

import java.util.List;

/**
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
public interface EventProcessor extends Runnable
{
    void submitEvents(List<Event> events);
}
