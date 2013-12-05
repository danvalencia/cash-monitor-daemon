package com.maquinet;

import com.maquinet.events.EventWatcher;
import com.maquinet.events.impl.StandardEventWatcher;

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
            EventWatcher watcher = new StandardEventWatcher(fileToWatch);
            watcher.watchFile();
        }
    }

    private static void usage()
    {
        System.out.println("Need to pass in the path to listen");
    }
}
