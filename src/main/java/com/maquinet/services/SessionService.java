package com.maquinet.services;

import com.maquinet.models.Session;
import com.maquinet.persistence.impl.SessionDAO;

import java.util.logging.Logger;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class SessionService
{
    private static final Logger LOGGER = Logger.getLogger("SessionService");

    private final SessionDAO sessionDAO;

    public SessionService(SessionDAO sessionDAO)
    {
        this.sessionDAO = sessionDAO;
    }

    public Session getCurrentSession()
    {
        LOGGER.info("About to get current session from DB");
        Session currentSession = sessionDAO.findFirst();
        if(currentSession == null)
        {
            LOGGER.fine("Current session is null");
        }
        return currentSession;
    }

    public void deleteCurrentSession()
    {
        boolean result = false;
        Session currentSession = getCurrentSession();
        LOGGER.info("About to get current session from DB");
        if(currentSession != null)
        {
            result = sessionDAO.delete(currentSession);
            LOGGER.fine(String.format("Result of deleting current session is %s", result));
        }
    }

    public boolean saveSession(Session session)
    {
        return sessionDAO.save(session);
    }
}
