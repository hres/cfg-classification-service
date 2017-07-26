# Canada Food Guide Classification Service

This project provides the Food Classification REST Service.
The service uses Drools Decision Table to implement the classification rules.
This service along with the [CFG Task Service]([cfg-task-services]) run in tandem.

## Components and Features

## How to Set up Eclipse Plugins

## Maven Build and Deployment

To deploy the [cfg-classification-services], do the following:

1. `cd ~/repositories`
2. `git clone https://github.com/hres/cfg-classification-service.git`
3. `cd cfg-classification-service`
4. `mvn clean install`
5. copy `target/cfg-classification-service.war` to `webapps` directory of [Tomcat 8.0 on HRES]

## Configure MongoDB

Copy and rename [mongodb.properties.template] to mongodb.properties

`cp mongdb.properties.template mongdb.properties`

Change the `host` and `port` properties in the mongodb.properties file in order to allow the Java API to connect to it.

## Confirm Service is Running

Run [Test]

[//]: # (These are the references links used in the body of this note and get stripped out when the markdown processor does its thing.  There is no need to format nicely because it should not be seen.)

[cfg-classification-services]: <https://github.com/hres/cfg-classification-service.git>
[cfg-task-services]:           <https://github.com/hres/cfg-task-service.git>
[Tomcat 8.0 on HRES]:          <https://java-dev.hres.ca>
[Test]:                        <https://java-dev.hres.ca/cfg-classification-service/test>
[mongodb.properties.template]: <https://github.com/hres/cfg-classification-service/blob/master/src/main/java/ca/gc/ip346/util/mongodb.properties.template>
