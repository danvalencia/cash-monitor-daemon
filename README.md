## Prerequisitos
El único prerequisito para correr el demonio es tener instalado el ambiente de ejecución de Java (JRE).  Para instalarlo en ubuntu basta correr el siguiente comando:

* sudo apt-get install openjdk-7-jre 

## Pasos para ejecutar el demonio de cash monitor:


1. Crear el siguiente directorio: /opt/cash-monitor, y asegurarse de tener permisos de escritura, lectura y ejecución. Nos referiremos a este directorio como $CASH_MONITOR_HOME.
2. Copiar el archivo cashmonitor-daemon.tar a $CASH_MONITOR_HOME y desde ahi extraerlo usando el siguiente comando:
```
#!bash
$ tar -xvf cashmonitor-daemon.tar
```

3. Desde $CASH_MONITOR_HOME ejecutar el siguiente comando:
```
#!bash
$ ./cashmonitor_daemon.sh 
```

## Descripción de los archivos


### *cashmonitor.properties*
Contiene las siguientes configuraciones:    
**machine.uuid**: El identificador único por maquinet. Éste es creado al dar de alta una maquinet por medio de la aplicación web.     
**cashmonitor.endpoint**: La URL del servicio web.     
**events.file**: La ruta al archivo de eventos.  Por default es: /opt/cash-monitor/files/events.txt 

### *log.properties*
Configuración para los logs, para efectos de diagnóstico y troubleshooting.  Aquí también se configura la ruta de los logs, que por default es: /opt/cash-monitor/logs/. 
Los logs son útiles para verificar que el demonio esté funcionando correctamente. 

### *cashmonitor_daemon.sh*
Este es un bash script que por ahora nada más corre el comando para ejecutar el demonio.  Asegurándose que tiene permisos de ejecución, este archivo se corre de la siguiente manera:
```
#!bash
$ ./cashmonitor_daemon.sh
```

### *bin/cash-monitor-daemon-0.1b-jar-with-dependencies.jar*
Este es el archivo jar que contiene el código del demonio.

## Diagnósticar el demonio

Para diagnosticar o verificar que el demonio esté funcionando correctamente, abre los archivos de logs que están localizados por default en $CASH_MONITOR_HOME/logs.
