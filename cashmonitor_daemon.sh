#!/bin/bash

LOG_PROPERTIES_FILE=log.properties
CASHMONITOR_PROPERTIES_FILE=cashmonitor.properties
file_pattern=`grep java.util.logging.FileHandler.pattern < $LOG_PROPERTIES_FILE | cut -d "=" -f 2`
logs_directory=`dirname $file_pattern`
echo "Starting up CashMonitor Daemon..."
nohup java -jar -Djava.util.logging.config.file=$LOG_PROPERTIES_FILE  -Dcashmonitor.properties=$CASHMONITOR_PROPERTIES_FILE bin/cash-monitor-daemon-0.1b-jar-with-dependencies.jar 1>/dev/null 2>&1  &
echo "CashMonitor Daemon has been started with pid $!"
echo "Log directory is $logs_directory"
