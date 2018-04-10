# samenmetenbridge
Bridge from dust sensor MQTT to Influx DB for project https://www.samenmetenaanluchtkwaliteit.nl/

See also the project page with more information at:
https://revspace.nl/DustSensor

# Building
## Linux
To build under Linux:
* install a jdk8 and the gradle tool (e.g. using your package manager)
* cd into directory workspace/gradle
* run 'gradle distTar'
* under directory workspace/samenmetenbridge/build/distributions you can find the installation .tar 
## Windows
To build under windows:
* install a JDK8 in directory tools
* cd into directory workspace/gradle
* run 'gradlew distZip'
* under directory workspace/samenmetenbridge/build/distributions you can find the installation .zip

# Configuring
Configuration options are stored in the file samenmetenbridge.properties

It contains (at the moment of writing this), the following options:
* sensor.lat: the latitude of the sensor location in degrees, e.g. 52.02264
* sensor.lon: the longitude of the sensor location in degrees, e.g. 4.69260
* mqtt.url: the URL of the MQTT server, e.g. tcp://aliensdetected.com
* mqtt.topic: the topic under which the data is published over MQTT
* samenmeten.url: the URL of the samenmeten influx database, e.g. http://influx.rivm.nl:8086
* samenmeten.id: the unique id of the sensor, part of the data written into the database
* samenmeten.user: the samenmeten influx database user name
* samenmeten.pass: the samenmeten influx database password
* luftdaten.url: the URL of the luftdaten HTTP API, e.g. https://api.luftdaten.info
* luftdaten.id: the unique id of the sensor, e.g. esp8266-123456
* luftdaten.timeout: the timeout (ms) for accessing the luftdaten API, e.g 3000
* luftdaten.version: the version string for accessing the luftdaten API, e.g. "0.1"

# Running
## Linux
To run the application:
* untar the installation .tar somewhere
  tar xvf samenmetenbridge.tar
* start the application, to auto-create a default configuration file
  ./samenmetenbridge.sh
* stop the application (ctrl-C) and edit the samenmetenbridge.properties configuration file
* re-run the application and verify it uses the correct settings
## Windows
* unzip the installation zip somewhere
* start the application, to auto-create a default configuration file
* stop the application (ctrl-C) and edit the samenmetenbridge.properties configuration file
* re-run the application and verify it uses the correct settings

# Logging
Normally, logging is sent to both stdout and into a log file.
The log file is called samenmetenbridge.log. It is a rotating log file with 10 files of 10 MB each.
Log file configuration can be found in cfg/log4j.properties.

