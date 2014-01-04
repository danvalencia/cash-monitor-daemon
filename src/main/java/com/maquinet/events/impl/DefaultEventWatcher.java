package com.maquinet.events.impl;

import com.maquinet.events.EventProcessor;
import com.maquinet.events.EventWatcher;
import com.maquinet.exceptions.EventWatcherException;
import com.maquinet.events.models.Event;
import com.maquinet.events.models.EventType;
import com.sun.nio.file.SensitivityWatchEventModifier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daniel Valencia (danvalencia@gmail.com)
 */
public class DefaultEventWatcher implements EventWatcher
{
    private static final Logger LOGGER = Logger.getLogger(DefaultEventWatcher.class.getName());

    private final WatchService watchService;
    private final Path fileToWatch;
    private final Path directoryToWatch;
    private final EventProcessor eventProcessor;

    public DefaultEventWatcher(String fileToWatch, EventProcessor eventProcessor)
    {
        LOGGER.info(String.format("File to watch is: %s", fileToWatch));

        try
        {
            this.eventProcessor = eventProcessor;
            this.watchService = FileSystems.getDefault().newWatchService();
            this.fileToWatch = FileSystems.getDefault().getPath(fileToWatch);

            if(this.fileToWatch.toFile().isDirectory())
            {
                throw new IllegalArgumentException(String.format("Path %s needs to be a file and it's a directory", fileToWatch));
            }

            this.directoryToWatch = this.fileToWatch.getParent();
        }
        catch (IOException e)
        {
            throw new EventWatcherException("There was an issue creating the watch service", e);
        }
    }

    @Override
    public void watchFile()
    {
        try
        {
            WatchEvent.Kind[] kinds = new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_MODIFY,
                                                            StandardWatchEventKinds.ENTRY_CREATE};
            this.directoryToWatch.register(this.watchService, kinds, SensitivityWatchEventModifier.HIGH);
            waitForFileChange();
        }
        catch (IOException e)
        {
            throw new EventWatcherException("There was an issue registering the directory to watch", e);
        }
    }

    private void waitForFileChange()
    {
        while (true)
        {
            WatchKey key;
            try
            {
                key = this.watchService.take();
            }
            catch (InterruptedException e)
            {
                throw new EventWatcherException("Exception taking the key from the watch service", e);
            }

            for (WatchEvent<?> pollEvent: key.pollEvents())
            {
                WatchEvent.Kind<?> kind = pollEvent.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) continue;

                WatchEvent<Path> event = (WatchEvent<Path>) pollEvent;

                Path path = event.context();

                if(path.equals(this.fileToWatch.getFileName()))
                {
                    retrieveEventsFromFile();
                }
            }

            // Reset the key -- this step is critical if you want to
            // receive further watch events.  If the key is no longer valid,
            // the directory is inaccessible so exit the loop.
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    private void retrieveEventsFromFile()
    {
        List<Event> events = new ArrayList<>();

        try
        {
            if(Files.exists(this.fileToWatch))
            {
                List<String> eventList = Files.readAllLines(this.fileToWatch, StandardCharsets.UTF_8);
                Files.deleteIfExists(this.fileToWatch);

                for (String eventString : eventList)
                {
                    List<String> eventAttributes = parseEventAttributes(eventString);

                    if(eventAttributes.size() > 0)
                    {
                        final String eventName = eventAttributes.get(0);

                        try
                        {
                            Event event = EventType.resolveEventType(eventName).createEvent(eventAttributes);
                            events.add(event);
                        }
                        catch (RuntimeException e)
                        {
                            LOGGER.log(Level.SEVERE, String.format("Ignoring creation of event with name %s because of exception", eventName), e);
                        }
                    }
                }

                this.eventProcessor.submitEvents(events);
            }
        }
        catch (FileNotFoundException e)
        {
            LOGGER.log(Level.SEVERE, String.format("File %s was not found, can't retrieve event", this.fileToWatch.toString()), e);
        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, String.format("There was an error reading the file %s", this.fileToWatch.toString()), e);
        }
    }

    private List<String> parseEventAttributes(String eventString)
    {
        String[] eventAttributesArray = eventString.split(Event.EVENT_STRING_SEPARATOR);
        return Arrays.asList(eventAttributesArray);
    }

}
