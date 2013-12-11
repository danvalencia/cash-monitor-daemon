package com.maquinet.events.processor.impl;

import com.maquinet.events.processor.EventProcessor;
import com.maquinet.models.Event;

import java.util.List;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class StandardEventProcessor implements EventProcessor
{
    @Override
    public void processEvents(List<Event> events)
    {
        for(Event event : events)
        {
            System.out.println(String.format("Event: %s; Type: %s", event.getName(), event.getEventType()));

        }
    }
}
