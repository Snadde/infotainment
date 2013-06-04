package se.chalmers.pd.playlistmanager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * This class controls the main flow of the application. It forwards connect requests and messages
 * to the other classes of the application. A lot of the work that is done is executed
 * asynchronously so it implements callbacks from the mqtt client and the dialogs
 * the controller can display.
 */
public class ApplicationController implements MqttWorker.Callback, DialogFactory.Callback {

    /**
     * Callbacks to the owner of the controller that triggers when messages are received.
     */
    public interface Callback {
        /**
         * Triggered when the client has received a message.
         *
         * @param action the action to perform
         */
        public void onMessageAction(Action action);

        /**
         * Triggered when the client has received a message.
         *
         * @param action the action to perform
         * @param t      the data that the actions needs to complete its action. This differs as follows;
         *               add: Track t
         *               add_all: ArrayList<Track> t
         *               seek: float t
         */
        public <T extends Object> void onMessageAction(Action action, T t);
    }

    private static final String TYPE_DATA = "data";
    private static final String TOPIC_PLAYLIST = "/playlist";
    private static final String TOPIC_PRIVATE = "/playlist/playlistmanager";
    private static final String TOPIC_SENSOR = "/sensor/infotainment";
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

    /**
     * Sets up the basic controls of the class.
     *
     * @param context  the context that the class is executed in. Used to show dialogs.
     * @param callback the class that needs information about incoming messages.
     */
    public ApplicationController(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
        mqttWorker = new MqttWorker(this);
    }

    /**
     * Builds a connect dialog which shows a connecting message to the user. It then
     * connects the mqtt client to the url.
     *
     * @param url the url to connect to
     */
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

    /**
     * Called from the mqtt client when the connection has been established or failed. If connection
     * has failed, it shows a connect dialog to the user.
     *
     * @param connected true if connected, false if not.
     */
    @Override
    public void onConnected(boolean connected) {
        if (connected) {
            mqttWorker.subscribe(TOPIC_PLAYLIST);
            mqttWorker.subscribe(TOPIC_PRIVATE);
            mqttWorker.subscribe(TOPIC_SENSOR);
            mqttWorker.publish(TOPIC_PLAYLIST, getAllJsonMessage());
            connectingDialog.dismiss();
            Log.d(TAG, "Now subscribing to " + TOPIC_PLAYLIST + ", " + TOPIC_PRIVATE + ", " + TOPIC_SENSOR);
        } else {
            ((MainActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogFactory.buildConnectToUrlDialog(context, ApplicationController.this, brokerUrl, R.string.reconnect_dialog_message).show();
                }
            });
        }
    }

    /**
     * Helper method that builds a "get_all" json message which is published
     * by the mqtt client when the application starts.
     *
     * @return a string representation of the message.
     */
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

    /**
     * Executed when the user has given an answer in the connect dialog. If the result
     * is positive, a new connection attempt will be made with the new broker url. If
     * not, the dialog is simply dismissed.
     *
     * @param result       true if the user wants to connect, false otherwise
     * @param newBrokerUrl the new url to connect to
     */
    @Override
    public void onConnectDialogAnswer(boolean result, String newBrokerUrl) {
        if (result) {
            connect(newBrokerUrl);
        } else {
            if (connectingDialog != null && connectingDialog.isVisible()) {
                connectingDialog.dismiss();
            }
        }
    }

    /**
     * Executed from the mqtt client when a message has been received. It checks what
     * kind of message has come in and triggers callbacks to the Callback implementer.
     * The actions have different datatypes that are required for their actions and
     * this data is constructed here.
     *
     * @param topic   the topic of the imcoming message
     * @param payload the message body as a json string
     */
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

    /**
     * Converts a json array of tracks to an array list of tracks.
     *
     * @param trackArray the json array of tracks
     * @return a list of Track objects
     * @throws JSONException if the json object does not meet the Track object required fields.
     */
    private ArrayList<Track> jsonArrayToTrackList(JSONArray trackArray) throws JSONException {
        ArrayList<Track> playlist = new ArrayList<Track>();
        for (int i = 0; i < trackArray.length(); i++) {
            JSONObject jsonTrack = trackArray.getJSONObject(i);
            Track track = jsonToTrack(jsonTrack);
            playlist.add(track);
        }
        return playlist;
    }

    /**
     * Converts a json object of a track to a real Track object.
     *
     * @param jsonTrack the json object to convert
     * @return the Track object that has been created
     * @throws JSONException if the json object does not meet the Track object required fields.
     */
    private Track jsonToTrack(JSONObject jsonTrack) throws JSONException {
        return new Track(jsonTrack.getString(TRACK_NAME), jsonTrack.getString(TRACK_ARTIST), jsonTrack.optString(TRACK_URI), jsonTrack.optInt(TRACK_LENGTH));
    }

    /**
     * Called when the user has selected a track from the search results and wish to add it to
     * the playlist. This method creates the json object and ask's the mqtt client to publish it.
     *
     * @param track the track to add to the playlist
     */
    public void addTrack(Track track) {
        if (mqttWorker != null) {
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

    /**
     * Tells the mqtt client to perfom an action. These actions are any of;
     * play, pause, prev, next.
     *
     * @param action the action to publish
     */
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

    /**
     * Helper method to build a json message with a given action.
     *
     * @param action the action to add to the 'action' field
     * @return a string representation of the json object
     */
    private String getJsonActionMessage(String action) {
        JSONObject json = new JSONObject();
        try {
            json.put(Action.action.toString(), action);
        } catch (JSONException e) {
            Log.e(TAG, "Could not create and send json object from action " + action + " with error: " + e.getMessage());
        }
        return json.toString();
    }

    /**
     * Fetches the current brokerUrl and returns it.
     *
     * @return the current broker url.
     */
    public String getBrokerUrl() {
        return brokerUrl;
    }

    /**
     * Called when the user has triggered a 'seek' action. It asks the mqtt client to publish a seek
     * message with the new position.
     *
     * @param position the position to seek to as a fraction number (ex. 0.73)
     */
    public void seek(float position) {
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
