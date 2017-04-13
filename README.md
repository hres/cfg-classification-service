# Food Classification Service POC

This project is a proof-of-concept for the Food Classification REST Service.  The service uses Drools Decision Table to implement the classification rules.    

## Components and Features

This POC uses the following components and features:

* Drools Decision Table (Excel)
* Java 8
* Maven 3.3.9
* Tomcat 8.0
* Eclipse Neon
* Drools Eclipse Plugin 
* Drools Runtime 6.5
* JAX-RS REST Service with Jersey and Jackson consuming and producing XML and JSON.

## How to Set up Eclipse Plugins

* Install GEF http://download.eclipse.org/tools/gef/updates/releases/
* Install Drools: http://download.jboss.org/drools/release/6.5.0.Final/org.drools.updatesite/

## Maven Build and Deployment

1. mvn clean install
2. rename target/*.war to food-classification-service-poc.war
3. move food-classification-service-poc.war to webapps directory of tomcat 8

## How to Test Service

The service speaks XML and JSON.  To get JSON, set the request header 'Accept' to 'application/json'.  To post JSON input, set the request header 'Content-Type' to 'application/json'.

* Retrieve an array of sample food items from http://localhost:8080/food-classification-service-poc/service/flags

`<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<tieredFoods>
    <tieredFood>
        <adjustedTier>1</adjustedTier>
        <exclusion1></exclusion1>
        <fatAdded>false</fatAdded>
        <label>dried, sweetened</label>
        <lowFat>false</lowFat>
        <lowSugar>false</lowSugar>
        <name>Crystalized Pineapple</name>
        <subGroup>112</subGroup>
        <sugarAdded>true</sugarAdded>
        <tsatTier>1</tsatTier>
    </tieredFood>
    <tieredFood>
        <adjustedTier>1</adjustedTier>
        <exclusion1></exclusion1>
        <fatAdded>false</fatAdded>
        <label>canned, sweetened</label>
        <lowFat>false</lowFat>
        <lowSugar>false</lowSugar>
        <name>Canned Peach</name>
        <subGroup>112</subGroup>
        <sugarAdded>true</sugarAdded>
        <tsatTier>1</tsatTier>
    </tieredFood>
    <tieredFood>
        <adjustedTier>1</adjustedTier>
        <exclusion1></exclusion1>
        <fatAdded>false</fatAdded>
        <label>canned, juice pack</label>
        <lowFat>false</lowFat>
        <lowSugar>false</lowSugar>
        <name>Canned Orange Juice</name>
        <subGroup>112</subGroup>
        <sugarAdded>false</sugarAdded>
        <tsatTier>1</tsatTier>
    </tieredFood>
    <tieredFood>
        <adjustedTier>1</adjustedTier>
        <exclusion1></exclusion1>
        <fatAdded>false</fatAdded>
        <label>canned, heavy syrup</label>
        <lowFat>false</lowFat>
        <lowSugar>false</lowSugar>
        <name>Heavy Syrup Pack</name>
        <subGroup>112</subGroup>
        <sugarAdded>true</sugarAdded>
        <tsatTier>1</tsatTier>
    </tieredFood>
</tieredFoods>`


* Set Food Flags

Use the sample array of food items to issue a POST request to URL: http://localhost:8080/food-classification-service-poc/service/flags
Set the request body to the sample array.  The returned sample food array should have some food flags set such as 'SugarAdded': true.


* Adjust Food Tiers

Use the returned array of food items from the flags step to issue a POST request to URL: http://localhost:8080/food-classification-service-poc/service/tier-adjustments
Set the request body to the sample array.  The returned sample food array should have food tiers adjusted.