package com.maquinet.events.exceptions;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class EventWatcherException extends RuntimeException
{
    public EventWatcherException(String message, Throwable throwable)
    {
        super(message, throwable);
    }
}
