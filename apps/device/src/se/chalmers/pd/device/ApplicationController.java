package se.chalmers.pd.device;

import org.json.JSONException;
import org.json.JSONObject;

import se.chalmers.pd.device.MqttWorker.MQTTCallback;
import se.chalmers.pd.device.SpotifyController.PlaylistCallback;
import android.content.Context;

public class ApplicationController implements MQTTCallback, PlaylistCallback {
	
	interface  Callbacks{
		void onPlayerLoggedIn();
		void onPlayerPlay();
		void onPlayerPause();
	}
	
	
	private Context context;
	private MqttWorker mqttWorker;
	private SpotifyController spotifyController; 
	private boolean debug = true;
	private Callbacks callbacks; //TODO implement callbacks to MainActivity for updating view
	
	public ApplicationController(Context context) {
		this.mqttWorker = new MqttWorker(this);
		this.context = context;
		this.callbacks = (Callbacks) context;
		spotifyController = new SpotifyController(this, context);
		
	}
	
	
	private void setupPlaylist() {
		JSONObject payload = new JSONObject();
		String[] artists = {"Foo Fighters", "Nirvana", "Avicii"};
		String[] tracks = {"The Pretender", "Rape me", "X You"};
		try {
			payload.put("action", "add");
			for (int i = 0; i < 3; i++){
				payload.put("track", tracks[i]);
				payload.put("artist", artists[i]);
				mqttWorker.publish("/playlist", payload.toString());		
			}	
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		spotifyController.addTrackToPlaylist("The pretender", "Foo Fighters", "spotify:track:3ZsjgLDSvusBgxGWrTAVto");
		
		spotifyController.addTrackToPlaylist("Rape me", "Nirvana", "spotify:track:47KVHb6cOVBZbmXQweE5p7");
		
		spotifyController.addTrackToPlaylist("X you", "Avicii", "spotify:track:330r0K82tIDVr6f1GezAd8");
		
	}
	
	public void login(){
		spotifyController.login();
	}
	
	public void play(){
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
	
	public void pause(){
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
	
	public void next(){
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
	
	public void previous(){
		//TODO implement this feature
		//spotifyController.playPrevious();
	}
	
	
	/**
	 * Controller method to publish a message
	 * 
	 * @param topic
	 *            of message
	 * @param payload
	 *            of message (should be stringified JSON)
	 */
	public void publish(String topic, String payload) {
		if(mqttWorker != null)
			mqttWorker.publish(topic, payload);
	}
	
	
//	/**
//	 * Forwards subscribe request to the mqttservice
//	 * 
//	 * @param topic
//	 */
//	public void subscribe(String topic) {
//		mqttWorker.subscribe(topic);
//	}
	

	/**
	 * Disconnects from the mqtt broker
	 */
	public void disconnect(){
		mqttWorker.disconnect();
	}
	
	/**
	 * Disconnects from the mqtt broker
	 */
	public void connect(){
		mqttWorker.connect();
	}
	

	public void onLoginSuccess() {
		callbacks.onPlayerLoggedIn();
	}


	public void onLoginFailed(String message) {
		// TODO Auto-generated method stub
		
	}


	public void onPlay(boolean success) {
		if(success){
			int index = spotifyController.getIndexOfCurrentTrack();
			Track currentTrack = spotifyController.getPlaylist().get(index);
		//	textview.setText("Currently Playing : " + currentTrack.getArtist() + " - " + currentTrack.getName());
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

	
	public void onExist(boolean success) {
		if(success){
			//TODO change buttons?
		}
		
	}


	public void onInstall(String result) {
		if(result.equals("success")){
			//TODO chnage buttons? 
		}else if(result.equals("error")){
			//TODO send feedback
		}else if(result.equals("pending")){
			//TODO animate something cool!
		}
		
	}


	public void onStart(String result) {
		if(result.equals("success")){
			
			setupPlaylist();
		}else if(result.equals("error")){
			//TODO send feedback
		}else if(result.equals("pending")){
			//TODO animate something cool!
		}

	}


	public void onStop(boolean success) {
		if(success){
			//TODO change buttons?
		}
		
	}


	public void onUninstall(boolean success) {
		if(success){
			//TODO change buttons?
		}
		
	}


}
