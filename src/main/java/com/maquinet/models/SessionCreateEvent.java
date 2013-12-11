package com.maquinet.models;

import java.util.List;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class SessionCreateEvent extends Event
{
    public SessionCreateEvent(List<String> attributes, EventType eventType)
    {
        super(attributes, eventType);
    }
}
