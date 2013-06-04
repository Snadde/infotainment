This Android  application can retrieve the current playlist from the primary device and display it to the user. The application also allows the user to search the Spotify Metadata API for tracks that can then be added to the 
shared playlist. This application can also control playback of the tracks on the primary device.

You can set the broker URL by using an NFC tag that is programmed with the broker URL string, for example: `tcp://192.168.1.10:1883`.

### Setup

* Clone the project if you haven't already
* Import into your IDE
* Add libs to your build path (Spotify Metadata API can be added as source)
* If not using NFC, change the BROKER_URL in MqttWorker. (You can also choose connect from options menu and enter URL manually)
* Make sure the broker is running
* Launch and connect

### Flow

1. User launches app and connects either through NFC or menu -> connect
2. A request is made to get all the tracks from the playlist from the primary device
3. When tracks are received they are added to playlist
4. User can now search and add to playlist or control playback

### Known issues

* User cannot select a specific song in playlist and launch it, not implemented yet.
* Looses state if primary device is playing an this device disconnects and comes back.

### Libs

* [Jackson annotations, core, databind](http://jackson.codehaus.org/)
* [Paho](http://www.eclipse.org/paho/)
* [Spotify Metadata API](https://github.com/hekoru/spotify-metadata-api/)
