package com.maquinet.persistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
public class EntityManagerUtils
{
    private static final EntityManagerFactory entityManagerFactory;

    static
    {
        entityManagerFactory = Persistence.createEntityManagerFactory("cashmonitor-ds");
    }

    public static EntityManagerFactory getEntityManagerFactory()
    {
        return entityManagerFactory;
    }
}
