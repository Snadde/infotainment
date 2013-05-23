The head unit application is also an Android application which has a WebView in which it displays and hosts the web application that is received from the primary device.

You need a web server running on the android device that is running this application. We use (kWS)[https://play.google.com/store/apps/details?id=org.xeustechnologies.android.kws&hl=en]

### Setup

* Download (kWS)[https://play.google.com/store/apps/details?id=org.xeustechnologies.android.kws&hl=en]
* Launch kWS and set web root to `/sdcard/www/`
* Clone the project if you haven’t already
* Import into your IDE
* Change the BROKER_URL in MqttWorker to your broker
* Make sure the broker and web server is running
* Launch the application

### Flow

1. User launches the application and it connects to the broker
2. When connected the WebView shows the launching/starting screen
3. The application is now awaiting communication from the primary device on the `/system`topic

### Known issues

* Not known

### Libs

* [kWS](https://play.google.com/store/apps/details?id=org.xeustechnologies.android.kws&hl=en)
* [Paho](http://www.eclipse.org/paho/)
