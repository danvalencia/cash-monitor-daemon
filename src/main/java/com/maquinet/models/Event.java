package com.maquinet.models;

import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
@Entity
@Immutable
@Table( name = "EVENTS" )
public class Event
{
    public static final String EVENT_STRING_SEPARATOR = ",";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private EventType eventType;

    private Long id;

    private String name;

    private Date eventCreationDate;

    private List<String> attributes;

    /**
     * Default constructor for hibernate purposes
     */
    public Event(){}

    public Event(List<String> attributes, EventType eventType)
    {
        this.attributes = attributes;
        this.eventType = eventType;
        initialize();
    }

    private void initialize()
    {
        this.name = attributes.get(0);
        this.eventCreationDate = parseEventCreationDate(attributes.get(1));
    }

    private Date parseEventCreationDate(String eventCreationString)
    {
        try
        {
            return new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).parse(eventCreationString);
        } catch (ParseException e)
        {
            throw new IllegalArgumentException(String.format("Issue parsing event creation date: %s", eventCreationString), e);
        }
    }

    // Getters and Setters
    @Id
    @GeneratedValue
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Date getEventCreationDate()
    {
        return eventCreationDate;
    }

    public void setEventCreationDate(Date eventCreationDate)
    {
        this.eventCreationDate = eventCreationDate;
    }

    public EventType getEventType()
    {
        return eventType;
    }

    public void setEventType(EventType eventType)
    {
        this.eventType = eventType;
    }

}
