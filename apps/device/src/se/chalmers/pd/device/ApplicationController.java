package se.chalmers.pd.device;

import org.json.JSONException;
import org.json.JSONObject;

import se.chalmers.pd.device.MqttWorker.MQTTCallback;
import se.chalmers.pd.device.SpotifyController.PlaylistCallback;
import android.content.Context;
/**
 * 
 * @author Patrik Thituson
 *
 */
public class ApplicationController implements MQTTCallback, PlaylistCallback {
	
	/**
	 * Callbacks that is used for the main activity to be able to update the view
	 * @author Snadde
	 *
	 */
	interface Callbacks {
		void onPlayerLoggedIn();

		void onPlayerPlay();
		
		void onPlayerNext();

		void onPlayerPause();

		void onStartedApplication(boolean show);

		void onInstalledApplication(boolean show);
		
		void onConnectedMQTT();
	}

	private Context context;
	private MqttWorker mqttWorker;
	private SpotifyController spotifyController;
	private boolean debug = true;
	private Callbacks callbacks; 

	public ApplicationController(Context context) {
		this.mqttWorker = new MqttWorker(this);
		this.context = context;
		this.callbacks = (Callbacks) context;
		spotifyController = new SpotifyController(this, context);
		setupPlaylist();
	}
	
	/**
	 * Helper method that adds a few tracks to the playlist for testing purposes.
	 */
	private void setupPlaylist() {
		spotifyController.addTrackToPlaylist("The pretender", "Foo Fighters", "spotify:track:3ZsjgLDSvusBgxGWrTAVto");
		spotifyController.addTrackToPlaylist("Rape me", "Nirvana", "spotify:track:47KVHb6cOVBZbmXQweE5p7");
		spotifyController.addTrackToPlaylist("X you", "Avicii", "spotify:track:330r0K82tIDVr6f1GezAd8");
	}
	
	/**
	 * Helper method that publish the playlist to the topic "/playlist" for testing purposes
	 */
	private void sendPlayList() {
		JSONObject payload = new JSONObject();
		String[] artists = { "Foo Fighters", "Nirvana", "Avicii" };
		String[] tracks = { "The Pretender", "Rape me", "X You" };
		try {
			payload.put("action", "add");
			for (int i = 0; i < 3; i++) {
				payload.put("track", tracks[i]);
				payload.put("artist", artists[i]);
				mqttWorker.publish("/playlist", payload.toString());
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Forwards he login calls to the spotifycontroller
	 */
	public void login() {
		spotifyController.login();
	}
	
	/**
	 * Publish a play message to the topic "/playlist" and 
	 * calls the spotify controller to start playing.
	 */
	public void play() {
		JSONObject payload = new JSONObject();
		try {
			payload.put("action", "play");
			mqttWorker.publish("/playlist", payload.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		spotifyController.play();
	}
	
	/**
	 * Publish a pause message to the topic "/playlist" and 
	 * calls the spotify controller to pause playing.
	 */
	public void pause() {
		JSONObject payload = new JSONObject();
		try {
			payload.put("action", "pause");
			mqttWorker.publish("/playlist", payload.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		spotifyController.pause();
	}
	/**
	 * Publish a next message to the topic "/playlist" and 
	 * calls the spotify controller to select the next song
	 */
	public void next() {
		spotifyController.playNext();
		JSONObject payload = new JSONObject();
		try {
			payload.put("action", "next");
			mqttWorker.publish("/playlist", payload.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void previous() {
		// TODO implement this feature
		// spotifyController.playPrevious();
	}



	/**
	 * Callback that let us know we have succesfully logged in to
	 * 	spotify
	 */
	public void onLoginSuccess() {
		callbacks.onPlayerLoggedIn();
	}
	
	/**
	 * Callback that let us know an error occured when logging in to
	 * 	spotify
	 */
	public void onLoginFailed(String message) {

	}
	
	/**
	 * Callback that is called when a song has succesfully started playing
	 */
	public void onPlay(boolean success) {
		if (success) {
			callbacks.onPlayerPlay();
		}

	}
	
	/**
	 * Callback that is called when a song has succesfully paused playing
	 */
	public void onPause(boolean success) {
		callbacks.onPlayerPause();
	}
	/**
	 * Callback that is called when a song has ended and calls the functon next
	 */
	public void onEndOfTrack() {
		next();
		callbacks.onPlayerNext();
	}
	
	/**
	 * Destroys the spotifycontroller and disconenct the broker to prevent any 
	 * threads from crashing
	 */
	public void onDestroy() {
		spotifyController.destroy();
		mqttWorker.disconnect();
	}

	/**
	 * Connects to the mqtt broker
	 */
	public void connect() {
		mqttWorker.connect();
	}
	
	/**
	 * Disconnects from the mqtt broker
	 */
	public void disconnect() {
		mqttWorker.disconnect();
	}
	
	/**
	 * Publish an exist message to the "/system" topic to see if an application 
	 * is installed, a callback is later called with the results
	 */
	public void exist(){
		String message = DeviceMessage.existMessage("playlist");
		mqttWorker.publish("/system", message);
	}
	/**
	 * Publish an start message to the "/system" topic to start an application 
	 * a callback is later called with the result
	 */
	public void start() {
		String message = DeviceMessage.startMessage("playlist");
		mqttWorker.publish("/system", message);
	}
	/**
	 * Publish an stop message to the "/system" topic to stop an application 
	 * a callback is later called with the result
	 */
	public void stop() {
		String message = DeviceMessage.stopMessage("playlist");
		mqttWorker.publish("/system", message);
	}
	/**
	 * Reads a zip file in the assets folder and convert it to base64 then
	 * publish an install message to the "/system" topic to install an application 
	 * a callback is later called with the result.
	 */
	public void install() {
		StreamToBase64String streamToBase64String = StreamToBase64String.getInstance(context);
		String data = streamToBase64String.getBase64StringFromAssets("Playlist.zip");
		String message = DeviceMessage.installMessage(data);
		mqttWorker.publish("/system", message);
	}
	/**
	 * Publish an uninstall message to uninstall the application
	 */
	public void uninstall() {
		String message = DeviceMessage.unInstallMessage("playlist");
		mqttWorker.publish("/system", message);
	}
	/**
	 * When the application is connected to the broker this callback is called
	 * and then an exist message is published to see if there is an application
	 * installed.
	 */
	public void onConnected() {
		callbacks.onConnectedMQTT();
		exist();
	}
	
	/**
	 * When an application exists this callback method is called and
	 * forward it to the callback in the main activity for updating the view.
	 */
	public void onExist(boolean success) {
		callbacks.onInstalledApplication(success);
	}

	
	/**
	 * When an application is started this callback method will publish a 
	 * playlist to the broker for testing purposes.
	 */
	public void onStart(String result) {
		if (result.equals("success")) {
			sendPlayList();
			callbacks.onStartedApplication(true);
		} else if (result.equals("error")) {
			// TODO send feedback
		} else if (result.equals("pending")) {
			// TODO animate something cool!
		}

	}
	
	/**
	 * When an application has stopped this callback method is called. 
	 * This method just calls a callback in the main acivity for updating the view.
	 */
	public void onStop(boolean success) {
			callbacks.onStartedApplication(false);
	}
	
	/**
	 * When an application is installing, installed or an error has occured
	 *  this callback is called.
	 * This method forwards the succesfully installed message to the main activity
	 * for updating the view
	 */
	public void onInstall(String result) {
		if (result.equals("success")) {
			callbacks.onInstalledApplication(true);
		} else if (result.equals("error")) {
			// TODO send feedback
		} else if (result.equals("pending")) {
			// TODO animate something cool!
		}

	}
	
	/**
	 * Forwards the onUninstall callback to the main activity 
	 * for updating the view.
	 */
	public void onUninstall(boolean success) {
		callbacks.onInstalledApplication(false);
	}
	
	/**
	 * Forwards the onActionPlay callback to the main activity 
	 * for updating the view.
	 */
	public void onActionPlay() {
		spotifyController.play();
		callbacks.onPlayerPlay();
	}
	
	/**
	 * Forwards the onActionPause callback to the main activity 
	 * for updating the view.
	 */
	public void onActionPause() {
		spotifyController.pause();
		callbacks.onPlayerPause();
	}
	
	/**
	 * Forwards the onActionNext callback to the main activity 
	 * for updating the view.
	 */
	public void onActionNext() {
		spotifyController.playNext();
		callbacks.onPlayerNext();
	}
	/**
	 * 
	 */
	public boolean isConnectedToBroker(){
		return mqttWorker.isConnected();
	}
	
	public String getCurrentTrack() {
		Track track = spotifyController.getCurrentTrack();		
		if(track!=null){
		return track.getArtist() + " - " + track.getName();
		}
		else
			return "No tracks available";
	}

}
