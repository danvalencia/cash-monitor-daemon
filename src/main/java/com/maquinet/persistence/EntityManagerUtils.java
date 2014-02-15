package com.maquinet.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
public class EntityManagerUtils
{
    private final EntityManagerFactory entityManagerFactory;
    private ThreadLocal<EntityManager> entityManagerThreadLocal;

    public EntityManagerUtils(EntityManagerFactory entityManagerFactory)
    {
        this.entityManagerFactory = entityManagerFactory;

        entityManagerThreadLocal = new ThreadLocal<EntityManager>(){
            @Override
            protected EntityManager initialValue()
            {
                return EntityManagerUtils.this.entityManagerFactory.createEntityManager();
            }
        };
    }

    public EntityManager getEntityManager()
    {
        return entityManagerThreadLocal.get();
    }

}
