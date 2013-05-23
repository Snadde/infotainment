## Quick info

* there are three android apps so you need three devices (secondary device is not required to get it up and running)
* apps are found in the apps folder 
* you need an mqtt broker running (preferably Mosquitto)
* you need to change broker ip’s in the apps before installing (at least on the head unit app since it doesn’t have an input field for ip in the application)
* you need a spotify premium account to play music
* if you need more information about an app, check out the application specific README
* there are diagrams and analysis in [the report](INSERT LINK)

## Background

Through the use of open source technology and free software this project has investigated ways of communicating between services on the internet, mobile devices and some central entity in a motor vehicle which is simulated using 
Android. We have created a set of sample applications that allow a user to push web applications from a primary device, into the simulated head unit. This web application and the primary device application can then be controlled 
by the vehicle sensors (simulated in simple Java app) and a secondary device (also using Android). 

The concept will allow for further research and expansion, which will allow drivers and passengers in a car to bring applications with them into a new environment where the car is seen as an i/o interface for mobile devices and 
their services. Feel free to use any content or information you find here.

We’ve also written a report on the project which can be [found here](INSERT LINK).

## Applications and requirements

We’ve come up with an imagined [use case scenario](https://github.com/Snadde/infotainment/wiki/use-cases) where two people share control of a Spotify playlist in a car. The playlist is also displayed in the head unit. Check 
the link above for more detailed information about the use case.

We use MQTT as the base communication protocol to get the different devices to talk to each other through a server (broker). During the project we experimented with using the “mqttjs” plugin for Node.JS for broker but we 
had some issues with it which we were not able to sort out. We recommend Mosquitto, it should work out of the box.

The project consists of five different applications.

### Primary device

This Android application is the device which hosts the web application that will be pushed into the head unit. It also controls the playback of a Spotify playlist. Note that you need a Spotify Premium account to be able to 
log in and play back tracks.

### Secondary device

This Android  application can retrieve the current playlist from the primary device and display it to the user. The application also allows the user to search the Spotify Metadata API for tracks that can then be added to the 
shared playlist. This application can also control playback of the tracks on the primary device.

### Head unit

The head unit application is also an Android application which has a WebView in which it displays and hosts the web application that is received from the primary device.

### Web application

The web application is a simple HTML5 app which receives information about which tracks are in the playlist and which track is being played. The app shows this information on a simple HTML page which is hosted by the WebView 
in the head unit. This application can also control playback on the primary device.

### Sensor gateway

The sensor application is a simple Java application which will simulate a “sensor gateway”. It allows the user to control playback when clicking some buttons (like on a steering wheel on a car).

