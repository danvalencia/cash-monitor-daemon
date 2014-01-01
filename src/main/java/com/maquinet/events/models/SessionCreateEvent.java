package com.maquinet.events.models;

import javax.persistence.Entity;
import java.util.List;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
@Entity
public class SessionCreateEvent extends Event
{
    public SessionCreateEvent(List<String> attributes, EventType eventType)
    {
        super(attributes, eventType);
    }
}
