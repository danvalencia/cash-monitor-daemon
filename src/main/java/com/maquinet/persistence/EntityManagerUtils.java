package com.maquinet.persistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
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
