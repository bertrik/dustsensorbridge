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

