The web application is a simple HTML5 app which receives information about which tracks are in the playlist and which track is being played. The app shows this information on a simple HTML page which is hosted by the WebView in the head unit. This application can also control playback on the primary device.

### Setup

* Clone the project if you haven’t already
* If you have made any changes to it, update the zip file in the primary device with a new Playlist.zip. Note that the built in compress tool in OS X is broken so you should use terminal instead: `zip -r Playlist.zip Playlist/`.

### Flow

* Application is launched by the head unit
* Application communicates through the JavaScriptInterface and asks the head unit to subscribe to the private topics
* Application waits for messages and updates GUI when it receives playlist or playback information.

### Known issues

* Double tapping can be tricky

### Libs

* jQuery
* jQuery plugin
* jQuery timer
