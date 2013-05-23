This Android application is the device which hosts the web application that will be pushed into the head unit. It also controls the playback of a Spotify playlist. Note that you need a Spotify Premium account to be able to log in and play tracks.

You can set the broker URL by using an NFC tag that is programmed with the broker URL string, for example: `tcp://192.168.1.10:1883`

### Setup

* Download Android SDK and NDK
* Install the plugins required for your IDE
* Import the project to your IDE
* Set your project settings to use native code
* Go to [Spotify developer](https://developer.spotify.com/technologies/libspotify/keys/) to request your Application key and store it in jni/key.h  
* Put your Spotify Premium username and password in a text file called userdetails.txt in the Assets folder  as follow:
`username,password`
*When building the project it should automatically build the native code first
*You can also build the project with the command ndk-build in the root of the project folder

### Flow

* User launches the application and connects with spotify through the userdetails.txt
* The user can connect with the broker by pressing the connect button and fill in the URI as follows:
`tcp://a.b.c.d:1883` were a.b.c.d is the ip address of the broker
* The user can also connect through a NFC tag if it contains the URI of the broker
* When the user is connected a status text will notify if there is an application installed in the Head unit
* If there is not the user can press the install button and wait for response
* The user can now start and stop the web-application in the head unit by using the on/off switch
* When the web-application is started you can listen to your music and use the buttons for play, pause. next and previous

### Known issues

Here are some known issues that we are aware of, note that this implementation is created with the idea of using the web-application as the playlist view.

* Without a connection to the broker the application can not play tracks
* There is no storage for the playlist
* There is no search and add tracks implemented, you have to use the secondary device for that
* The login info to spotify is stored in a textfile, could be a login page instead?
* The Playlist is not visible in the application

### Libs

* [Paho](http://www.eclipse.org/paho/)
* [libspotify](https://developer.spotify.com/technologies/libspotify/)