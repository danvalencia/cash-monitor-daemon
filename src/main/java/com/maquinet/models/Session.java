package com.maquinet.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
@Entity
@Table(name = "SESSIONS")
public class Session
{
    private String sessionUuid;
    private Long id;

    private Date createdDate = new Date();

    public Session(String sessionUuid)
    {
        this.sessionUuid = sessionUuid;
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
}
