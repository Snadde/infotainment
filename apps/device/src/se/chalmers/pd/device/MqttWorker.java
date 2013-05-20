package se.chalmers.pd.device;

import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttDefaultFilePersistence;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;

/**
 * This service launches an MQTT client in a separate thread and subscribes to
 * the systems basic topics. When a message is received, it broadcasts the
 * message using custom intent filters.
 * 
 */
public class MqttWorker {

	private static final String STORAGE_DIRECTORY = "/infotainment/";
	private static final String SERVICE_NAME = "MqttWorker";

	public static final String ACTION_DATA = "data";
	public static final String ACTION_INSTALL = "install";
	public static final String ACTION = "action";
	public static final String TOPIC_SYSTEM = "/system";
	public static final String ACTION_EXIST = "exist";
	public static final String ACTION_SUCCESS = "success";
	public static final String ACTION_ERROR = "error";
	public static final String ACTION_TYPE = "type";
	public static final String ACTION_RESPONSE = "response";
	public static final String ACTION_START = "start";
	public static final String ACTION_UNINSTALL = "uninstall";
	public static final String ACTION_STOP = "stop";

	public static final String MQTT_STATUS_INTENT = "se.chalmers.pd.dashboard.mqtt.STATUS";
	public static final String MQTT_STATUS_MESSAGE = "se.chalmers.pd.dashboard.mqtt.STATUS_MESSAGE";
	public static final String MQTT_MESSAGE_RECEIVED_INTENT = "se.chalmers.pd.dashboard.mqtt.MESSAGE_RECEIVED";
	public static final String MQTT_MESSAGE_RECEIVED_TOPIC = "se.chalmers.pd.dashboard.mqtt.MESSAGE_RECEIVED_TOPIC";
	public static final String MQTT_MESSAGE_RECEIVED_PAYLOAD = "se.chalmers.pd.dashboard.mqtt.MESSAGE_RECEIVED_PAYLOAD";

	//private static final String BROKER = "tcp://192.168.43.147:1883";
	private static final String CLIENT_NAME = "device";

	private MqttClient mqttClient;
	private String data;

	private ArrayList<MQTTCallback> mQTTCallbacks = new ArrayList<MQTTCallback>();
	private Thread mqttClientThread;

	public interface MQTTCallback {
		void onExist(boolean success);

		void onInstall(String result);

		void onStart(String result);

		void onStop(boolean success);

		void onUninstall(boolean success);

		void onConnected();

		void onActionPlay();

		void onActionPause();

		void onActionNext();
		
		void onActionAdd(Track newTrack);
	}

	public MqttWorker(MQTTCallback mQTTCallback) {
		mQTTCallbacks.add(mQTTCallback);
	}

	/**
	 * Connects to the MQTT broker in a new Thread and defines some custom
	 * callback that handle messages.
	 */
	public void connect(final String url) {
		if (mqttClientThread != null && mqttClientThread.isAlive()) {
			try {
				mqttClient.disconnect();
				mqttClient.connect();
			} catch (MqttException e) {
				e.printStackTrace();
			}
		} else {
			mqttClientThread = new Thread(new Runnable() {

				public void run() {
					try {
						// Sets up the client and subscribes to topics
						// TODO Find external temp dir for client
						String tmpDir = Environment.getExternalStorageDirectory() + STORAGE_DIRECTORY;
						MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
						mqttClient = new MqttClient(url, CLIENT_NAME, dataStore);
						mqttClient.setCallback(new CustomMqttCallback());
						mqttClient.connect();
						mqttClient.subscribe("/playlist/1");
						mqttClient.subscribe("/playlist");
						mqttClient.subscribe("/sensor/infotainment");
						for (MQTTCallback callback : mQTTCallbacks) {
							callback.onConnected();
						}
						Log.d(SERVICE_NAME, "subscribing");
					} catch (MqttException e) {
						e.printStackTrace();
					}
				}

				/**
				 * Called when messages are received.
				 */
				class CustomMqttCallback implements MqttCallback {

					public void messageArrived(MqttTopic topic, MqttMessage message) throws Exception {
						JSONObject json = new JSONObject(message.toString());

						Log.d(SERVICE_NAME,
								"messageArrived" + "topic:" + topic.toString() + ", message:" + message.toString());
						notifyCallbacks(json);
					}
					/**
					 * 	Calls different callbacks pending on what action it is in the Json object, 
					 *  there is also a check for the type "response" for the system messages.
					 * @param json
					 */
					private void notifyCallbacks(JSONObject json) {

						String action = json.optString("action");
						String type = json.optString("type");
						String data = json.optString("data");
						boolean success = data.equals("success");

						for (MQTTCallback callback : mQTTCallbacks) {

							if (type.equals("response")) {
								if (action.equals("exist")) {
									callback.onExist(success);
								} else if (action.equals("install")) {
									callback.onInstall(data);
								} else if (action.equals("start")) {
									callback.onStart(data);
								} else if (action.equals("stop")) {
									callback.onStop(success);
								} else if (action.equals("uninstall")) {
									callback.onUninstall(success);
								}
							} else {
								if (action.equals("play")) {
									callback.onActionPlay();
								} else if (action.equals("pause")) {
									callback.onActionPause();
								} else if (action.equals("next")) {
									callback.onActionNext();
								} else if(action.equals("add")){
									String name = json.optString("track");
									String artist = json.optString("artist");
									String spotifyUri = json.optString("uri");
									int length = json.optInt("tracklength");
									Track newTrack = new Track(name, artist, spotifyUri, length);
									callback.onActionAdd(newTrack);
								}
							}
						}

					}

					public void deliveryComplete(MqttDeliveryToken token) {
						Log.d(SERVICE_NAME, "deliveryComplete " + "token:" + token);
					}

					public void connectionLost(Throwable cause) {
						Log.d(SERVICE_NAME, "connectionLost " + "cause:" + cause.toString());
					}
				}
			}, SERVICE_NAME);
			mqttClientThread.start();
		}

	}

	/**
	 * Helper method to disconnect from broker
	 */
	public void disconnect() {
		try {
			mqttClient.disconnect();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Publishes a message on the given topic.
	 * 
	 * @param topic
	 * @param message
	 *            should be stringified JSON
	 */
	public void publish(String topic, String message) {
		Log.d("MqttWorker", "publishing topic " + topic + " with message " + message);
		try {
			MqttMessage payload = new MqttMessage(message.getBytes());
			// mqttClient.getTopic(topic).publish(payload);
			mqttClient.getTopic(topic).publish(payload.getPayload(), 2, false);
		} catch (MqttPersistenceException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Subscribes to the given topic
	 * 
	 * @param topic
	 */
	public void subscribe(String topic) {
		try {
			mqttClient.subscribe(topic);
		} catch (MqttSecurityException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Unsubscribes from the given topic
	 * 
	 * @param topic
	 */
	public void unsubscribe(String topic) {
		try {
			mqttClient.unsubscribe(topic);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the data for the application install
	 * 
	 * @return
	 */
	public String getApplicationRawData() {
		return data;
	}
	/**
	 * 
	 * @return true if client is connected to broker
	 */
	public boolean isConnected(){
		if(mqttClient != null)
			return mqttClient.isConnected();
		else
			return false;
	}

}
