package com.maquinet.models;

import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Arrays;
import java.util.List;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
@Entity
@Immutable
@Table( name = "EVENTS" )
public class MaquinetEvent
{
    private static final String SEPARATOR = ",";
    private Long id;

    private String name;

    private List<String> attributes;
    /**
     * Default constructor for hibernate purposes
     */
    public MaquinetEvent()
    {

    }

    public MaquinetEvent(String eventString)
    {
        initialize(eventString);
    }

    private void initialize(String eventString)
    {
        String[] eventAttributesArray = eventString.split(SEPARATOR);
        if (eventAttributesArray.length > 0)
        {
            List<String> eventAttributes = Arrays.asList(eventAttributesArray);
            this.name = eventAttributes.get(0);
            this.attributes = eventAttributes.subList(1, eventAttributes.size());
        } else
        {
            throw new IllegalArgumentException("Event String contains zero attributes: " + eventString);
        }
    }

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


}
