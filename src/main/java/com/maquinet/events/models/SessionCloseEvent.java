package com.maquinet.events.models;

import javax.persistence.Entity;
import java.util.List;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
@Entity
public class SessionCloseEvent extends Event
{
    public SessionCloseEvent(List<String> attributes, EventType eventType)
    {
        super(attributes, eventType);
    }
}
