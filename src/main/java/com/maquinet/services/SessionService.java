package com.maquinet.services;

import com.maquinet.models.Session;
import com.maquinet.persistence.impl.SessionDAO;

import java.util.logging.Logger;

/**
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
public class SessionService
{
    private static final Logger LOGGER = Logger.getLogger(SessionService.class.getName());

    private final SessionDAO sessionDAO;

    public SessionService(SessionDAO sessionDAO)
    {
        this.sessionDAO = sessionDAO;
    }

    public Session getCurrentSession()
    {
        return sessionDAO.findFirst();
    }

    public boolean deleteCurrentSession()
    {
        boolean result = true;
        Session currentSession = getCurrentSession();
        if(currentSession != null)
        {
            result = sessionDAO.delete(currentSession);
        }
        return result;
    }

    public boolean saveSession(Session session)
    {
        return sessionDAO.save(session);
    }
}
