package se.chalmers.pd.device;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.chalmers.pd.device.MqttWorker.MQTTCallback;
import se.chalmers.pd.device.SpotifyController.PlaylistCallback;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Patrik Thituson
 * 
 */
public class ApplicationController implements MQTTCallback, PlaylistCallback {

	/**
	 * Callbacks that is used for the main activity to be able to update the
	 * view
	 */
	interface Callbacks {
		void onPlayerLoggedIn();

		void onPlayerPlay();

		void onPlayerNext();

		void onPlayerPause();

		void onStartedApplication(String status);

		void onInstalledApplication(boolean show);

		void onConnectedMQTT(boolean connected);

        void onDisconnectedMQTT(boolean success);

		void onUpdateSeekbar(float position);

        void onUpdatedPlaylist();

	}

	private Context context;
	private MqttWorker mqttWorker;
	private SpotifyController spotifyController;
	private Callbacks callbacks;
    private final String TOPIC_PLAYLIST = "/playlist",
                         TOPIC_PLAYLIST_1 = "/playlist/1",
                         TOPIC_INFOTAINMENT = "/sensor/infotainment",
                         TOPIC_SYSTEM = "/system",
                         PLAYLIST_ZIP = "Playlist.zip",
                         PLAYLIST_NAME = "playlist",
                         DATA = "data",
                         ARTIST = "artist",
                         TRACK = "track",
                         TRACK_LENGTH = "tracklength",
                         URI = "uri";

	public ApplicationController(Context context) {

		this.context = context;
		this.callbacks = (Callbacks) context;
		spotifyController = new SpotifyController(this, context);
        mqttWorker = new MqttWorker(this);
	}

	/**
	 * Helper method that publish the playlist to the topic "/playlist" for
	 * testing purposes
	 */
	private void sendPlayList() {
        List<Track> newPlaylist = new ArrayList<Track>();
		String[] artists = { "Foo Fighters", "Nirvana", "Avicii" };
		String[] tracks = { "The Pretender", "Rape me", "X You" };
		String[] uris = { "spotify:track:3ZsjgLDSvusBgxGWrTAVto", "spotify:track:47KVHb6cOVBZbmXQweE5p7",
				"spotify:track:330r0K82tIDVr6f1GezAd8" };
		int[] lengths = { 270, 170, 200};
		for (int i = 0; i < 3; i++) {
			Track track = new Track(tracks[i], artists[i], uris[i], lengths[i]);
			newPlaylist.add(track);
		}
        sendAllTracks(TOPIC_PLAYLIST, newPlaylist);
	}

	/**
	 * Forwards he login calls to the spotifycontroller
	 */
	public void login() {
		spotifyController.login();
	}


	/**
	 * Callback that let us know we have succesfully logged in to spotify
	 */
	public void onLoginSuccess() {
		callbacks.onPlayerLoggedIn();
	}

	/**
	 * Callback that let us know an error occured when logging in to spotify
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
		createAndPublishPlayerActions(Action.next);
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
	public void connect(String url) {
        mqttWorker = new MqttWorker(this);
        mqttWorker.setBrokerURL(url);
		mqttWorker.start();
	}

	/**
	 * Disconnects from the mqtt broker
	 */
	public void disconnect() {
		mqttWorker.disconnect();
        mqttWorker.interrupt();
	}

    /**
     * Returns the current track selected in the playlist if
     * existing, the string "No tracks available" otherwise
     * @return
     */
	public String getCurrentTrack() {
		Track track = spotifyController.getCurrentTrack();
		if (track != null) {
			return track.getArtist() + " - " + track.getName();
		} else
			return context.getString(R.string.no_tracks_available);
	}

    /**
     * Publish a seek message to the 'playlist' topic
     * @param position
     */
	public void seek(float position) {
        JSONObject json = createJson(Action.seek, String.valueOf(position));
        mqttWorker.publish(TOPIC_PLAYLIST, json.toString());
	}

    /**
     * Callback from spotifycontroller that allows us to simulate
     * timer.
     * @param position
     */
	public void onPositionChanged(float position) {
		callbacks.onUpdateSeekbar(position);
	}
	
	/**
	 * Callback from Mqttworker thath handles all action-messages and performs
     * the corresponding actions. Uses callbacks to notify the main activity that
     * the state of the application has changed and an update of the UI could be
     * required.
	 * and perform the corresponding action. 
	 */
	public void onMessage(String topic, String payload) {
		JSONObject json;
		try {
			json = new JSONObject(payload);
			String action = json.optString(Action.action.toString());
			String data = json.optString(ActionResponse.data.toString());
            boolean success = data.equals(ActionResponse.success.toString());
			switch (Action.valueOf(action)) {
			case add:
				addJsonTrackToPlaylist(json);
				break;
			case play:
				spotifyController.play();
				break;
			case pause:
				spotifyController.pause();
				break;
			case next:
				spotifyController.playNext();
				callbacks.onPlayerNext();
				break;
            case prev:
                spotifyController.playPrevious();
                callbacks.onPlayerNext();
                break;
			case install:
				if (success) {
					callbacks.onInstalledApplication(true);	
				} else if (data.equals(ActionResponse.error.toString())) {
					callbacks.onInstalledApplication(false);
				} 
				break;
			case start:
				if (success) {
					sendPlayList();
                }
                callbacks.onStartedApplication(data);
				break;
			case stop:
                spotifyController.pause();
                spotifyController.clearPlaylist();
                callbacks.onStartedApplication(action);
				break;
			case uninstall:
				callbacks.onInstalledApplication(!success);
				break;
			case exist:
				callbacks.onInstalledApplication(success);
				break;
            case get_all:
                    sendAllTracks(data, spotifyController.getPlaylist());
                break;
			case add_all:
				JSONArray playlistArray = new JSONArray(data);
				JSONObject jsonTrack;
				for (int i = 0; i < playlistArray.length(); i++){
					jsonTrack = new JSONObject(playlistArray.get(i).toString());
					addJsonTrackToPlaylist(jsonTrack);
				}
				break;
            case seek:
                 spotifyController.seek(Float.parseFloat(data));
                 break;
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	/**
	 * Helper method for adding a track to the playlist based
	 * on the json object.
	 * @param jsonTrack
	 */
	private void addJsonTrackToPlaylist(JSONObject jsonTrack){
		String name = jsonTrack.optString(TRACK);
		String artist = jsonTrack.optString(ARTIST);
		String spotifyUri = jsonTrack.optString(URI);
		int length = jsonTrack.optInt(TRACK_LENGTH);
		Track newTrack = new Track(name, artist, spotifyUri, length);
		spotifyController.addTrackToPlaylist(newTrack);
        callbacks.onUpdatedPlaylist();
	}

    /**
     * Creates a system action message and publish it on the private topic
     * with the application name in the data field except for 'install' where
     * the web application as a base64 string will be stored instead.
     * @param action
     */
    public void createAndPublishSystemActions(Action action){
        String data = PLAYLIST_NAME;
        if(action.equals(Action.install)){
            StreamToBase64String streamToBase64String = StreamToBase64String.getInstance(context);
            data = streamToBase64String.getBase64StringFromAssets(PLAYLIST_ZIP);
        }
        JSONObject json = createJson(action, data);
        mqttWorker.publish(TOPIC_SYSTEM, json.toString());
    }

    /**
     * Creates a JSONObject based on the action and the data
     * @param action
     * @param data
     * @return
     */
    public JSONObject createJson(Action action, String data)
    {
        JSONObject json = new JSONObject();
        try {
            json.put(Action.action.toString(), action.toString());
            json.put(ActionResponse.data.toString(), data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Creates a player action message and publish it on the private channel
     * @param action
     */
    public void createAndPublishPlayerActions(Action action){
        JSONObject json = createJson(action, "");
        mqttWorker.publish(TOPIC_PLAYLIST, json.toString());
    }

    /**
     * Publish All tracks present in the Playlist of the spotifycontroller to the specified topic.
     * Gets the playlist and convert the tracks to Json objects and put them in a Json array.
     * @param topic
     *              the topic to which the message should be published
     */
    public void sendAllTracks(String topic, List<Track> tracks){
        JSONObject payload = new JSONObject();
        JSONArray playlistArray = new JSONArray();
        try {
            payload.put(Action.action.toString(), Action.add_all.toString());
            for(Track track: tracks){
                JSONObject jsonTrack = new JSONObject();
                jsonTrack.put(TRACK, track.getName());
                jsonTrack.put(ARTIST, track.getArtist());
                jsonTrack.put(URI, track.getUri());
                jsonTrack.put(TRACK_LENGTH, track.getLength());
                playlistArray.put(jsonTrack);
            }
            payload.put(DATA, playlistArray);
            mqttWorker.publish(topic, payload.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

	
	/**
	 * When the application is connected to the broker this callback is called
	 * and then an exist message is published to see if there is an application
	 * installed.
	 */
	public void onConnected(boolean connected) {
		callbacks.onConnectedMQTT(connected);
		if(connected){
            mqttWorker.subscribe(TOPIC_PLAYLIST);
            mqttWorker.subscribe(TOPIC_PLAYLIST_1);
            mqttWorker.subscribe(TOPIC_INFOTAINMENT);
            createAndPublishSystemActions(Action.exist);
        }
	}

    /**
     * When a disconnect from the broker has been tried this callback method is
     * called with the result and notifies the view (activity).
     * @param success
     */
    @Override
    public void onDisconnected(boolean success) {
        callbacks.onDisconnectedMQTT(success);
    }

}
