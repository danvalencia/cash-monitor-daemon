package com.maquinet.events.models;

import javax.persistence.Entity;
import java.util.List;

/**
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
@Entity
public class ConfigurationUpdateEvent extends Event
{
    private Long coinValue;
    private Long coinTime;

    public ConfigurationUpdateEvent(){}

    public ConfigurationUpdateEvent(List<String> attributes, EventType eventType)
    {
        super(attributes, eventType);
        setCoinValue(Long.parseLong(attributes.get(2)));
        setCoinTime(Long.parseLong(attributes.get(3)));
    }

    public Long getCoinValue()
    {
        return coinValue;
    }

    public void setCoinValue(Long coinValue)
    {
        this.coinValue = coinValue;
    }

    public Long getCoinTime()
    {
        return coinTime;
    }

    public void setCoinTime(Long coinTime)
    {
        this.coinTime = coinTime;
    }
}
