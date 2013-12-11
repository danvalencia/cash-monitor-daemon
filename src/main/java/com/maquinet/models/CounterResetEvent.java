package com.maquinet.models;

import java.util.List;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class CounterResetEvent extends Event
{
    public CounterResetEvent(List<String> attributes, EventType eventType)
    {
        super(attributes, eventType);
    }
}
