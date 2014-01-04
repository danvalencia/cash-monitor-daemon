package com.maquinet.events.models;

import javax.persistence.Entity;
import java.util.List;

/**
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
@Entity
public class EmptyEvent extends Event
{
    public EmptyEvent(){}

    public EmptyEvent(List<String> attributes, EventType eventType)
    {
        super(attributes, eventType);
    }
}
