package com.maquinet.events.watcher.impl;

import com.maquinet.events.processor.EventProcessor;
import com.maquinet.events.watcher.EventWatcher;
import com.maquinet.exceptions.EventWatcherException;
import com.maquinet.models.Event;
import com.maquinet.models.EventType;
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

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class DefaultEventWatcher implements EventWatcher
{
    private final WatchService watchService;
    private final Path fileToWatch;
    private final Path directoryToWatch;
    private final EventProcessor eventProcessor;

    public DefaultEventWatcher(String fileToWatch, EventProcessor eventProcessor)
    {
        System.out.println(String.format("File to watch is: %s", fileToWatch));
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
        } catch (IOException e)
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
        } catch (IOException e)
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
            } catch (InterruptedException e)
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
                    System.out.println(String.format("This is the file I'm interested in: %s", path.toString()));
                    System.out.println(String.format("Event: %s", kind));
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
            List<String> eventList = Files.readAllLines(this.fileToWatch, StandardCharsets.UTF_8);
            Files.deleteIfExists(this.fileToWatch);
            for (String eventString : eventList)
            {
                List<String> eventAttributes = parseEventAttributes(eventString);
                if(eventAttributes.size() > 0)
                {
                    final String eventName = eventAttributes.get(0);
                    Event event = EventType.resolveEventType(eventName).createEvent(eventAttributes);
                    events.add(event);
                }
            }
            this.eventProcessor.submitEvents(events);
        } catch (FileNotFoundException e)
        {
            System.out.println(String.format("File %s was not found, can't retrieve event", this.fileToWatch.toString()));
        } catch (IOException e)
        {
            System.out.println(String.format("There was an error reading the file %s", this.fileToWatch.toString()));
        }
    }

    private static List<String> parseEventAttributes(String eventString)
    {
        String[] eventAttributesArray = eventString.split(Event.EVENT_STRING_SEPARATOR);
        return Arrays.asList(eventAttributesArray);
    }

}
