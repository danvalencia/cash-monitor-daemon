package com.maquinet.events.processor;

import com.maquinet.models.Event;

import java.util.List;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public interface EventProcessor
{
    void processEvents(List<Event> events);
}
