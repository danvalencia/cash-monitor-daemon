package com.maquinet;

import com.maquinet.events.processor.EventProcessor;
import com.maquinet.events.processor.impl.StandardEventProcessor;
import com.maquinet.models.Event;
import com.maquinet.persistence.EntityManagerUtils;
import com.maquinet.events.watcher.EventWatcher;
import com.maquinet.events.watcher.impl.StandardEventWatcher;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

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
//            EntityManager entityManager = EntityManagerUtils.getEntityManagerFactory().createEntityManager();
//            Event event = new Event("sesion_creada,2013-12-05 05:29:29", );
//            EntityTransaction transaction = entityManager.getTransaction();
//            transaction.begin();
//            entityManager.persist(event);
//            transaction.commit();

//            System.out.println("Event ID: " + event.getId());

            EventProcessor eventProcessor = new StandardEventProcessor();
            EventWatcher watcher = new StandardEventWatcher(fileToWatch, eventProcessor);
            watcher.watchFile();

        }
    }

    private static void usage()
    {
        System.out.println("Need to pass in the path to listen");
    }
}
