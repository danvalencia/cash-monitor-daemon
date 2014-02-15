package com.maquinet.persistence.impl;

import com.maquinet.models.Session;
import com.maquinet.persistence.EntityDAO;
import com.maquinet.persistence.EntityManagerUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
public class SessionDAO implements EntityDAO<Session>
{
    private final EntityManagerUtils entityManagerUtils;

    private static final Logger LOGGER = Logger.getLogger(SessionDAO.class.getName());

    public SessionDAO(EntityManagerUtils entityManagerUtils)
    {
        this.entityManagerUtils = entityManagerUtils;
    }

    @Override
    public Session get(int id)
    {
        return getEntityManager().find(Session.class, id);
    }

    @Override
    public List<Session> findAll()
    {
        List<Session> eventList = (List<Session>) getEntityManager().createQuery("SELECT s FROM Session s").getResultList();
        return eventList;
    }

    @Override
    public Session findFirst()
    {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM Session s " +
                        "ORDER BY s.createdDate");
        query.setMaxResults(1);
        List<Session> events = query.getResultList();
        Session firstSession = null;
        if(events.size() > 0)
        {
            firstSession = events.get(0);
        }
        return firstSession;
    }

    @Override
    public boolean saveAll(List<Session> sessions)
    {
        boolean result = false;
        EntityTransaction transaction = getEntityManager().getTransaction();
        try
        {
            transaction.begin();
            for(Session session : sessions)
            {
                getEntityManager().persist(session);
            }
            transaction.commit();
            result = true;
        } catch (Exception e)
        {
            transaction.rollback();
            LOGGER.severe(String.format("There was an exception trying to persist sessions %s", sessions));
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean save(Session session)
    {
        boolean result = false;
        EntityTransaction transaction = getEntityManager().getTransaction();
        try
        {
            transaction.begin();
            getEntityManager().persist(session);
            transaction.commit();
            result = true;
        } catch (Exception e)
        {
            transaction.rollback();
            LOGGER.severe(String.format("There was an exception trying to persist session %s", session));
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean delete(Session session)
    {
        boolean result = false;
        LOGGER.info("Starting transaction");
        EntityTransaction transaction = getEntityManager().getTransaction();
        try
        {
            transaction.begin();
            getEntityManager().remove(session);
            transaction.commit();
            result = true;
        } catch (Exception e)
        {
            transaction.rollback();
            LOGGER.severe(String.format("Unable to delete session %s", session));
            e.printStackTrace();
        }
        return result;
    }

    EntityManager getEntityManager()
    {
        return entityManagerUtils.getEntityManager();
    }
}
