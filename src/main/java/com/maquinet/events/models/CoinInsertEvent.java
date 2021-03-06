package com.maquinet.events.models;

import javax.persistence.Entity;
import java.util.List;

/**
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
@Entity
public class CoinInsertEvent extends Event
{
    private Long globalCoinCount = new Long(0);

    public CoinInsertEvent(){}

    public CoinInsertEvent(List<String> attributes, EventType eventType)
    {
        super(attributes, eventType);
        setGlobalCoinCount(Long.parseLong(attributes.get(2)));
    }

    public Long getGlobalCoinCount()
    {
        return globalCoinCount;
    }

    public void setGlobalCoinCount(Long globalCoinCount)
    {
        this.globalCoinCount = globalCoinCount;
    }
}
