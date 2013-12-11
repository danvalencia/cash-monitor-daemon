package com.maquinet.models;

import java.util.List;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class SessionCloseEvent extends Event
{
    public SessionCloseEvent(List<String> attributes, EventType eventType)
    {
        super(attributes, eventType);
    }
}
