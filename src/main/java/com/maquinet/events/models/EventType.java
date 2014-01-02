package com.maquinet.events.models;

import com.maquinet.commands.Command;
import com.maquinet.commands.impl.EmptyCommand;
import com.maquinet.commands.impl.CoinInsertCommand;
import com.maquinet.commands.impl.ConfigurationUpdateCommand;
import com.maquinet.commands.impl.CounterResetCommand;
import com.maquinet.commands.impl.SessionCloseCommand;
import com.maquinet.commands.impl.SessionCreateCommand;
import com.maquinet.services.HttpService;
import com.maquinet.services.EventService;
import com.maquinet.services.SessionService;

import java.util.List;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public enum EventType
{
    SESSION_CREATE("sesion_creada")
    {
        @Override
        public Event createEvent(List<String> eventAttributes)
        {
            return new SessionCreateEvent(eventAttributes, this);
        }

        @Override
        public Command createCommand(Event event)
        {
            return new SessionCreateCommand(getHttpService(), getSessionService(), getEventService(), event);
        }
    },
    SESSION_CLOSE("sesion_cerrada")
    {
        @Override
        public Event createEvent(List<String> eventAttributes)
        {
            return new SessionCloseEvent(eventAttributes, this);
        }

        @Override
        public Command createCommand(Event event)
        {
            return new SessionCloseCommand(getHttpService(), getSessionService(), getEventService(), event);
        }
    },
    COIN_INSERT("moneda_insertada")
    {
        @Override
        public Event createEvent(List<String> eventAttributes)
        {
            return new CoinInsertEvent(eventAttributes, this);
        }

        @Override
        public Command createCommand(Event event)
        {
            return new CoinInsertCommand(getHttpService(), getSessionService(), getEventService(), event);
        }
    },
    COUNTER_RESET("contador_reseteado")
    {
        @Override
        public Event createEvent(List<String> eventAttributes)
        {
            return new CounterResetEvent(eventAttributes, this);
        }

        @Override
        public Command createCommand(Event event)
        {
            return new CounterResetCommand(getHttpService(), getSessionService(), getEventService(), event);
        }
    },
    CONFIG_UPDATE("configuracion_actualizada")
    {
        @Override
        public Event createEvent(List<String> eventAttributes)
        {
            return new ConfigurationUpdateEvent(eventAttributes, this);
        }

        @Override
        public Command createCommand(Event event)
        {
            return new ConfigurationUpdateCommand(getHttpService(), getEventService(), event);
        }
    },
    EMPTY_EVENT("")
    {
        @Override
        public Event createEvent(List<String> eventAttributes)
        {
            return new EmptyEvent(eventAttributes, this);
        }

        @Override
        public Command createCommand(Event event)
        {
            return new EmptyCommand(getHttpService(), getSessionService(), getEventService(), event);
        }
    };

    private String name;

    private SessionService sessionService;

    private HttpService httpService;
    private EventService eventService;
    private EventType(String name)
    {
        this.name = name;
    }

    public static EventType resolveEventType(String eventName)
    {
        for (EventType eventType: values())
        {
            if(eventType.name.equals(eventName))
            {
                return eventType;
            }
        }
        return EMPTY_EVENT;
    }

    public SessionService getSessionService()
    {
        return sessionService;
    }

    public HttpService getHttpService()
    {
        return httpService;
    }

    public EventService getEventService()
    {
        return eventService;
    }

    public void setSessionService(SessionService sessionService)
    {
        this.sessionService = sessionService;
    }

    public void setEventService(EventService eventService)
    {
        this.eventService = eventService;
    }

    public void setHttpService(HttpService httpService)
    {
        this.httpService = httpService;
    }

    public abstract Event createEvent(List<String> eventAttributes);

    public abstract Command createCommand(Event event);

}
