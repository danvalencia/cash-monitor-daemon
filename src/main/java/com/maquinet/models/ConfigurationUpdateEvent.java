package com.maquinet.models;

import java.util.List;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class ConfigurationUpdateEvent extends Event
{
    public ConfigurationUpdateEvent(List<String> attributes, EventType eventType)
    {
        super(attributes, eventType);
    }
}
