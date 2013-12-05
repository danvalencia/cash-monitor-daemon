package com.maquinet.events.impl;

import com.maquinet.events.EventWatcher;
import com.maquinet.events.exceptions.EventWatcherException;
import com.sun.nio.file.SensitivityWatchEventModifier;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class StandardEventWatcher implements EventWatcher
{
    private final WatchService watchService;
    private final Path fileToWatch;
    private final Path directoryToWatch;

    public StandardEventWatcher(String fileToWatch)
    {
        System.out.println(String.format("File to watch is: %s", fileToWatch));
        try
        {
            this.watchService = FileSystems.getDefault().newWatchService();
            this.fileToWatch = FileSystems.getDefault().getPath(fileToWatch);
            if(this.fileToWatch.toFile().isDirectory())
            {
                throw new IllegalArgumentException(String.format("Path %s needs to be a file and it's a directory"));
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
            WatchEvent.Kind[] kinds = new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE};
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
            WatchKey key = this.watchService.poll();

            if (key != null)
            {
                for (WatchEvent<?> pollEvent: key.pollEvents())
                {
                    System.out.println("Poll Event: " + pollEvent);
                    WatchEvent.Kind<?> kind = pollEvent.kind();

    //                if (kind == StandardWatchEventKinds.OVERFLOW) continue;

                    WatchEvent<Path> event = (WatchEvent<Path>) pollEvent;

                    Path path = event.context();

                    System.out.println(String.format("Path modified: %s", path.toString()));
                    System.out.println(String.format("Event: %s", kind));

                    if(kind == StandardWatchEventKinds.ENTRY_CREATE)
                    {

                    }
                    else if(kind == StandardWatchEventKinds.ENTRY_MODIFY)
                    {

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
    }
}
