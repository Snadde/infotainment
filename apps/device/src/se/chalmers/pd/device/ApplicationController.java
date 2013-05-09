package se.chalmers.pd.device;

import org.json.JSONException;
import org.json.JSONObject;

import se.chalmers.pd.device.MqttWorker.MQTTCallback;
import se.chalmers.pd.device.SpotifyController.PlaylistCallback;
import android.content.Context;

public class ApplicationController implements MQTTCallback, PlaylistCallback {

	interface Callbacks {
		void onPlayerLoggedIn();

		void onPlayerPlay();

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

	private void setupPlaylist() {
		spotifyController.addTrackToPlaylist("The pretender", "Foo Fighters", "spotify:track:3ZsjgLDSvusBgxGWrTAVto");
		spotifyController.addTrackToPlaylist("Rape me", "Nirvana", "spotify:track:47KVHb6cOVBZbmXQweE5p7");
		spotifyController.addTrackToPlaylist("X you", "Avicii", "spotify:track:330r0K82tIDVr6f1GezAd8");
	}

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

	public void login() {
		spotifyController.login();
	}

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




	public void onLoginSuccess() {
		callbacks.onPlayerLoggedIn();
	}

	public void onLoginFailed(String message) {

	}

	public void onPlay(boolean success) {
		if (success) {
			int index = spotifyController.getIndexOfCurrentTrack();
			Track currentTrack = spotifyController.getPlaylist().get(index);
			// textview.setText("Currently Playing : " +
			// currentTrack.getArtist() + " - " + currentTrack.getName());
			callbacks.onPlayerPlay();
		}

	}

	public void onPause(boolean success) {
		callbacks.onPlayerPause();
	}

	public void onEndOfTrack() {
		next();
	}

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
	
	public void exist(){
		String message = DeviceMessage.existMessage("playlist");
		mqttWorker.publish("/system", message);
	}

	public void start() {
		String message = DeviceMessage.startMessage("playlist");
		mqttWorker.publish("/system", message);
	}
	
	public void stop() {
		String message = DeviceMessage.stopMessage("playlist");
		mqttWorker.publish("/system", message);
	}

	public void install() {
		StreamToBase64String streamToBase64String = StreamToBase64String.getInstance(context);
		String data = streamToBase64String.getBase64StringFromAssets("Playlist.zip");
		String message = DeviceMessage.installMessage(data);
		mqttWorker.publish("/system", message);
	}
	
	public void uninstall() {
		String message = DeviceMessage.unInstallMessage("playlist");
		mqttWorker.publish("/system", message);
	}
	
	public void onConnected() {
		callbacks.onConnectedMQTT();
		exist();
	}
	

	public void onExist(boolean success) {
		callbacks.onInstalledApplication(success);
	}

	

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

	public void onStop(boolean success) {
			callbacks.onStartedApplication(false);
	}
	
	public void onInstall(String result) {
		if (result.equals("success")) {
			callbacks.onInstalledApplication(true);
		} else if (result.equals("error")) {
			// TODO send feedback
		} else if (result.equals("pending")) {
			// TODO animate something cool!
		}

	}
	
	public void onUninstall(boolean success) {
		callbacks.onInstalledApplication(false);
	}

	public void onActionPlay() {
		spotifyController.play();
	}

	public void onActionPause() {
		spotifyController.pause();
	}

	public void onActionNext() {
		spotifyController.playNext();
	}



}
