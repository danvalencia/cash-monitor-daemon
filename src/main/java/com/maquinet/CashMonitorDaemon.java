package com.maquinet;

import com.maquinet.models.MaquinetEvent;
import com.maquinet.persistence.EntityManagerUtils;
import com.maquinet.events.watcher.EventWatcher;
import com.maquinet.events.watcher.impl.StandardEventWatcher;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.Transaction;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class CashMonitorDaemon
{
    public static void main(String[] args)
    {
        if(args.length < 1)
        {
            usage();
            Runtime.getRuntime().exit(1);
        }
        else
        {
            String fileToWatch = args[0];
            EntityManager entityManager = EntityManagerUtils.getEntityManagerFactory().createEntityManager();
            MaquinetEvent event = new MaquinetEvent("sesion_creada,2013-12-05 05:29:29");
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(event);
            transaction.commit();

            System.out.println("Event ID: " + event.getId());

            EventWatcher watcher = new StandardEventWatcher(fileToWatch);
            watcher.watchFile();

        }
    }

    private static void usage()
    {
        System.out.println("Need to pass in the path to listen");
    }
}
