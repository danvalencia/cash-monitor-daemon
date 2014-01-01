#!/bin/bash

nohup java -jar -Djava.util.logging.config.file=log.properties -Dcashmonitor.properties=cashmonitor.properties cash-monitor-daemon-0.1b-jar-with-dependencies.jar 1>/dev/null 2>&1  &