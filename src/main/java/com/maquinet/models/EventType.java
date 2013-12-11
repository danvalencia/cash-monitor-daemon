package com.maquinet.models;

import java.util.List;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public enum EventType
{
    SESSION_CREATE("sesion_creada")
    {
        @Override
        public Event create(List<String> eventAttributes)
        {
            return new SessionCreateEvent(eventAttributes, this);
        }
    },
    SESSION_CLOSE("sesion_cerrada")
    {
        @Override
        public Event create(List<String> eventAttributes)
        {
            return new SessionCloseEvent(eventAttributes, this);
        }
    },
    COIN_INSERT("moneda_insertada")
    {
        @Override
        public Event create(List<String> eventAttributes)
        {
            return new CoinInsertEvent(eventAttributes, this);
        }
    },
    COUNTER_RESET("contador_reseteado")
    {
        @Override
        public Event create(List<String> eventAttributes)
        {
            return new CounterResetEvent(eventAttributes, this);
        }
    },
    CONFIG_UPDATE("configuracion_actualizada")
    {
        @Override
        public Event create(List<String> eventAttributes)
        {
            return new ConfigurationUpdateEvent(eventAttributes, this);
        }
    },
    EMPTY_EVENT("")
    {
        @Override
        public Event create(List<String> eventAttributes)
        {
            return new EmptyEvent(eventAttributes, this);
        }
    };

    private String name;

    EventType(String name)
    {
        this.name = name;
    }

    public abstract Event create(List<String> eventAttributes);

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

}
