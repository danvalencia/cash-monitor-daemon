package com.maquinet.models;

import javax.persistence.Entity;
import java.util.List;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
@Entity
public class EmptyEvent extends Event
{
    public EmptyEvent(List<String> attributes, EventType eventType)
    {
        super(attributes, eventType);
    }
}
