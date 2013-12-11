package com.maquinet.models;

import java.util.List;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class CoinInsertEvent extends Event
{
    public CoinInsertEvent(List<String> attributes, EventType eventType)
    {
        super(attributes, eventType);
    }
}
