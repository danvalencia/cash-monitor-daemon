package com.maquinet.models;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class Machine
{
    private String uuid;

    public Machine(String uuid)
    {
        this.uuid = uuid;
    }

    public String getUuid()
    {
        return uuid;
    }
}
