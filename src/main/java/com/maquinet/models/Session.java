package com.maquinet.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
@Entity
@Table(name = "SESSIONS")
public class Session
{
    private String sessionUuid;
    private Long id;
    private Long coinCount = new Long(0);
    private Date createdDate = new Date();
    private Date endDate;

    public Session() {}

    public Session(String sessionUuid)
    {
        this.sessionUuid = sessionUuid;
    }

    public void incrementCoinCount()
    {
        coinCount++;
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

    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }

    public Date getCreatedDate()
    {
        return createdDate;
    }

    public String getSessionUuid()
    {
        return sessionUuid;
    }

    public void setSessionUuid(String sessionUuid)
    {
        this.sessionUuid = sessionUuid;
    }

    public Long getCoinCount()
    {
        return coinCount;
    }

    public void setCoinCount(Long coinCount)
    {
        this.coinCount = coinCount;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }
}
