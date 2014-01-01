package com.maquinet.events.processor.impl;

import com.maquinet.commands.Command;
import com.maquinet.events.processor.EventProcessor;
import com.maquinet.events.models.Event;
import com.maquinet.events.models.EventType;
import com.maquinet.services.EventService;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class DefaultEventProcessor implements EventProcessor
{
    private static final Logger LOGGER = Logger.getLogger("DefaultEventProcessor");

    private final EventService eventService;

    private final Lock lock;

    private final Condition noEventsCondition;


    public DefaultEventProcessor(EventService eventService)
    {
        this.eventService = eventService;
        lock = new ReentrantLock();
        noEventsCondition = lock.newCondition();
    }

    @Override
    public void submitEvents(List<Event> events)
    {
        lock.lock();
        try
        {
            eventService.saveAllEvents(events);
            noEventsCondition.signal();
        }finally
        {
            lock.unlock();
        }
    }

    @Override
    public void run()
    {
        while (true)
        {
            lock.lock();
            try
            {
                Event event = eventService.getFirstEvent();
                if(event == null)
                {
                    try
                    {
                        noEventsCondition.await(10, TimeUnit.SECONDS);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }else
                {
                    EventType eventType = event.getEventType();
                    LOGGER.info(String.format("Found Event with type: %s", eventType));
                    Command command = eventType.createCommand(event);
                    LOGGER.info(String.format("About to execute command %s for event %s", command, event.getEventType()));
                    command.run();
                }
            }finally
            {
                lock.unlock();
            }
        }


    }
}
