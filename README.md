# Canada Food Guide Classification Service

This project provides the Food Classification REST Service.
The service uses Drools Decision Table to implement the classification rules.
This service along with the CFG Task Service run in tandem.

## Components and Features

## How to Set up Eclipse Plugins

## Maven Build and Deployment

To deploy the [cfg-classification-services], do the following:

1. `cd ~/repositories`
2. `git clone https://github.com/hres/cfg-classification-service.git`
3. `cd cfg-classification-service`
4. `mvn clean install`
5. copy `target/cfg-classification-service.war` to `webapps` directory of [Tomcat 8.0 on HRES]

[//]: # (These are the references links used in the body of this note and get stripped out when the markdown processor does its thing.  There is no need to format nicely because it should not be seen.)

[cfg-classification-services]: <https://github.com/hres/cfg-classification-service.git>
[Tomcat 8.0 on HRES]: <https://java-dev.hres.ca>
