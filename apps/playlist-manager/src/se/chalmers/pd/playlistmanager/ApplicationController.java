package se.chalmers.pd.playlistmanager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class ApplicationController implements MqttWorker.Callback, DialogFactory.Callback {

    public interface Callback {
		public void onMessageAction(Action action);
        public <T extends Object> void onMessageAction(Action action, T t);
	}

	private static final String TYPE_DATA = "data";
	private static final String TOPIC_PLAYLIST = "/playlist";
	private static final String TOPIC_PRIVATE = "/playlist/playlistmanager";
	private static final String TAG = "ApplicationController";
	private static final String TRACK_URI = "uri";
	private static final String TRACK_NAME = "track";
	private static final String TRACK_ARTIST = "artist";
	private static final String TRACK_LENGTH = "tracklength";

    private String brokerUrl = "";
    private MqttWorker mqttWorker;
	private Context context;
	private Callback callback;
    private LoadingDialogFragment connectingDialog;


	public ApplicationController(Context context, Callback callback) {
		this.context = context;
		this.callback = callback;
		mqttWorker = new MqttWorker(this);
	}
	
	public synchronized void connect(String url) {
        connectingDialog = DialogFactory.buildConnectingDialog(context, url);
        connectingDialog.show(((FragmentActivity) context).getFragmentManager(), "connectingDialog");
        brokerUrl = url;
		mqttWorker.disconnect();
		mqttWorker.interrupt();
		mqttWorker = new MqttWorker(this);
		mqttWorker.setUrl(url);
		mqttWorker.start();
	}
	
	@Override
	public void onConnected(boolean connected) {
		if(connected) {
			mqttWorker.subscribe(TOPIC_PLAYLIST);
			mqttWorker.subscribe(TOPIC_PRIVATE);
			mqttWorker.publish(TOPIC_PLAYLIST, getAllJsonMessage());
            connectingDialog.dismiss();
			Log.d(TAG, "Now subscribing to " + TOPIC_PLAYLIST + ", " + TOPIC_PRIVATE);
		} else { 
			((MainActivity) context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					DialogFactory.buildConnectToUrlDialog(context, ApplicationController.this, brokerUrl, R.string.reconnect_dialog_message).show();
				}
			});
		}
	}
	
	private String getAllJsonMessage() {
		JSONObject message = new JSONObject();
		try {
			message.put(Action.action.toString(), Action.get_all.toString());
			message.put(TYPE_DATA, TOPIC_PRIVATE);
		} catch (JSONException e) {
			Log.d(TAG, "Could not create get_all message: " + e.getMessage());
		}
		return message.toString();
	}
	
	@Override
	public void onConnectDialogAnswer(boolean result, String newBrokerUrl) {
		if(result) {
			connect(newBrokerUrl);
		} else {
            if(connectingDialog != null && connectingDialog.isVisible()) {
                connectingDialog.dismiss();
            }
        }
	}

	@Override
	public void onMessage(String topic, String payload) {
		try {
			JSONObject json = new JSONObject(payload);
			String actionString = json.getString(Action.action.toString());
			Action action = Action.valueOf(actionString);
            switch (action) {
                case add:
                    Track track = jsonToTrack(json);
                    callback.onMessageAction(action, track);
                    break;
                case add_all:
                    JSONArray trackArray = json.getJSONArray(TYPE_DATA);
                    callback.onMessageAction(action, jsonArrayToTrackList(trackArray));
                    break;
                case seek:
                    float position = Float.parseFloat(json.getString(TYPE_DATA));
                    callback.onMessageAction(action, position);
                    break;
                default:
                    callback.onMessageAction(action);
                    break;
			}
		} catch (JSONException e) {
			Log.e(TAG, "Could not create json object from payload " + payload + " with error: " + e.getMessage());
		}
	}

    private ArrayList<Track> jsonArrayToTrackList(JSONArray trackArray) throws JSONException {
		ArrayList<Track> playlist = new ArrayList<Track>();
		for(int i = 0; i < trackArray.length(); i++) {
			JSONObject jsonTrack = trackArray.getJSONObject(i);
			Track track = jsonToTrack(jsonTrack);
			playlist.add(track);
		}
        return playlist;
	}

	private Track jsonToTrack(JSONObject jsonTrack) throws JSONException {
		return new Track(jsonTrack.getString(TRACK_NAME), jsonTrack.getString(TRACK_ARTIST), jsonTrack.optString(TRACK_URI), jsonTrack.optInt(TRACK_LENGTH));
	}

	public void addTrack(Track track) {
		if(mqttWorker != null) {
			JSONObject message = new JSONObject();
			try {
				message.put(Action.action.toString(), Action.add.toString());
				message.put(TRACK_ARTIST, track.getArtist());
				message.put(TRACK_NAME, track.getName());
				message.put(TRACK_URI, track.getUri());
				message.put(TRACK_LENGTH, track.getLength());
				mqttWorker.publish(TOPIC_PLAYLIST, message.toString());
			} catch (JSONException e) {
				Log.e(TAG, "Could not create and send json object from track " + track.toString() + " with error: " + e.getMessage());
			}
		} else {
			Toast.makeText(context, R.string.cant_add_tracks_connect_to_broker, Toast.LENGTH_LONG).show();
		}
	}
	
	public void performAction(Action action) {
		switch (action) {
		case play:
		case pause:
		case prev:
		case next:
			mqttWorker.publish(TOPIC_PLAYLIST, getJsonActionMessage(action.toString()));
			break;
		default:
			break;
		}
	}

	private String getJsonActionMessage(String action) {
		JSONObject json = new JSONObject();
		try {
			json.put(Action.action.toString(), action);
		} catch (JSONException e) {
			Log.e(TAG, "Could not create and send json object from action " + action + " with error: " + e.getMessage());
		}
		return json.toString();
	}

    public String getBrokerUrl() {
        return brokerUrl;
    }
    
    public void seek(float position){
    	JSONObject json = new JSONObject();
    	try {
			json.put(Action.action.toString(), Action.seek);
			json.put(TYPE_DATA, String.valueOf(position));
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	mqttWorker.publish(TOPIC_PLAYLIST, json.toString());
    }

}
