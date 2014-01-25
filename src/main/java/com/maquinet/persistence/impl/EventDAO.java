package com.maquinet.persistence.impl;

import com.maquinet.events.models.Event;
import com.maquinet.persistence.EntityDAO;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
public class EventDAO implements EntityDAO<Event>
{
    private static final Logger LOGGER = Logger.getLogger(EventDAO.class.getName());

    private final EntityManager entityManager;

    public EventDAO(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    @Override
    public Event get(int id)
    {
        return entityManager.find(Event.class, id);
    }

    @Override
    public List<Event> findAll()
    {
        List<Event> eventList = (List<Event>)entityManager.createQuery("SELECT e FROM Event e").getResultList();
        return eventList;
    }

    @Override
    public Event findFirst()
    {
        Query query = entityManager.createQuery(
                "SELECT e FROM Event e " +
                        "ORDER BY e.id");
        query.setMaxResults(1);

        List<Event> events = query.getResultList();

        Event firstEvent = null;
        if(events.size() > 0)
        {
            firstEvent = events.get(0);
        }
        return firstEvent;
    }

    @Override
    public boolean save(Event event)
    {
        boolean result = false;
        EntityTransaction transaction = entityManager.getTransaction();
        try
        {
            transaction.begin();
            entityManager.persist(event);
            transaction.commit();
            result = true;
        } catch (Exception e)
        {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, String.format("There was an exception trying to persist event %s", event), e);
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean delete(Event event)
    {
        boolean result = false;
        EntityTransaction transaction = entityManager.getTransaction();
        try
        {
            transaction.begin();
            entityManager.remove(event);
            transaction.commit();
            result = true;
        } catch (Exception e)
        {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, String.format("Unable to delete event %s", event), e);
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean saveAll(List<Event> events)
    {
        boolean result = false;
        if(events.size() > 0)
        {
            EntityTransaction transaction = entityManager.getTransaction();
            try
            {
                transaction.begin();
                for(Event event : events)
                {
                    entityManager.persist(event);
                }
                transaction.commit();
                result = true;
            } catch (Exception e)
            {
                transaction.rollback();
                LOGGER.log(Level.SEVERE, String.format("There was an exception trying to persist events %s", events), e);
                e.printStackTrace();
            }
        }
        return result;
    }
}
