package com.maquinet.models;

import java.util.List;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class EmptyEvent extends Event
{
    public EmptyEvent(List<String> attributes, EventType eventType)
    {
        super(attributes, eventType);
    }
}
