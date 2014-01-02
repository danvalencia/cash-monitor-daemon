package com.maquinet;

import com.maquinet.events.EventProcessor;
import com.maquinet.events.impl.DefaultEventProcessor;
import com.maquinet.events.impl.DefaultEventWatcher;
import com.maquinet.services.HttpService;
import com.maquinet.services.impl.CashMonitorHttpService;
import com.maquinet.events.models.EventType;
import com.maquinet.persistence.EntityManagerUtils;
import com.maquinet.events.EventWatcher;
import com.maquinet.persistence.impl.EventDAO;
import com.maquinet.persistence.impl.SessionDAO;
import com.maquinet.services.EventService;
import com.maquinet.services.SessionService;
import org.apache.http.impl.client.HttpClients;

import javax.persistence.EntityManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import static com.maquinet.CashMonitorProperties.*;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class CashMonitorDaemon
{
    private static final Logger LOGGER = Logger.getLogger(CashMonitorDaemon.class.getName());

    public static void main(String[] args)
    {
        loadAndValidateSystemProperties();

        String fileToWatch = System.getProperty(EVENTS_FILE);
        EntityManager entityManager = EntityManagerUtils.getEntityManagerFactory().createEntityManager();

        EventDAO eventDAO = new EventDAO(entityManager);
        EventService eventService = new EventService(eventDAO);
        SessionDAO sessionDAO = new SessionDAO(entityManager);
        SessionService sessionService = new SessionService(sessionDAO);

        String cashmonitorEndpoint = System.getProperty(CASHMONITOR_ENDPOINT);
        HttpService httpService = new CashMonitorHttpService(HttpClients.createDefault(), cashmonitorEndpoint);

        initEventTypes(sessionService, httpService, eventService);

        EventProcessor eventProcessor = new DefaultEventProcessor(eventService);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(eventProcessor);

        EventWatcher watcher = new DefaultEventWatcher(fileToWatch, eventProcessor);
        watcher.watchFile();

    }

    private static void loadAndValidateSystemProperties()
    {
        String propertiesFilePath = System.getProperty(CASHMONITOR_PROPERTIES);
        if(propertiesFilePath == null)
        {
            usage();
            System.exit(1);
        }

        try
        {
            Properties systemProperties = System.getProperties();
            systemProperties.load(new FileInputStream(propertiesFilePath));
            if(systemProperties.get(MACHINE_UUID) == null)
            {
                LOGGER.severe(String.format("Property %s is required", MACHINE_UUID));
                System.exit(1);
            }
            else if(systemProperties.get(CASHMONITOR_ENDPOINT) == null)
            {
                LOGGER.severe(String.format("Property %s is required", CASHMONITOR_ENDPOINT));
                System.exit(1);
            }
            else if(systemProperties.get(EVENTS_FILE) == null)
            {
                LOGGER.severe(String.format("Property %s is required", EVENTS_FILE));
                System.exit(1);
            }


        }
        catch (IOException e)
        {
            LOGGER.severe(String.format("Unable to load properties file with path %s", CASHMONITOR_PROPERTIES));
            System.exit(1);
        }
    }

    private static void initEventTypes(SessionService sessionService, HttpService httpService, EventService eventService)
    {
        for(EventType eventType : EventType.values())
        {
            eventType.setSessionService(sessionService);
            eventType.setHttpService(httpService);
            eventType.setEventService(eventService);
        }
    }

    private static void usage()
    {
        System.out.println("Usage: ");
        System.out.println(String.format("       java -jar -D%s=/path/to/properties/file CashMonitorDaemon.jar", CASHMONITOR_PROPERTIES));
    }
}
