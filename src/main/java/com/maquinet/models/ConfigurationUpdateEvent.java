package com.maquinet.models;

import javax.persistence.Entity;
import java.util.List;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
@Entity
public class ConfigurationUpdateEvent extends Event
{
    public ConfigurationUpdateEvent(List<String> attributes, EventType eventType)
    {
        super(attributes, eventType);
    }
}
