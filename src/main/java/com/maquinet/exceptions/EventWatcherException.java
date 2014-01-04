package com.maquinet.exceptions;

/**
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
public class EventWatcherException extends RuntimeException
{
    public EventWatcherException(String message, Throwable throwable)
    {
        super(message, throwable);
    }
}
