package se.chalmers.pd.headunit;

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
import org.json.JSONException;
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
	public static final String ACTION_PENDING = "pending";
	
	public static final String MQTT_STATUS_INTENT = "se.chalmers.pd.headunit.mqtt.STATUS";
	public static final String MQTT_STATUS_MESSAGE = "se.chalmers.pd.headunit.mqtt.STATUS_MESSAGE";
	public static final String MQTT_MESSAGE_RECEIVED_INTENT = "se.chalmers.pd.headunit.mqtt.MESSAGE_RECEIVED";
	public static final String MQTT_MESSAGE_RECEIVED_TOPIC = "se.chalmers.pd.headunit.mqtt.MESSAGE_RECEIVED_TOPIC";
	public static final String MQTT_MESSAGE_RECEIVED_PAYLOAD = "se.chalmers.pd.headunit.mqtt.MESSAGE_RECEIVED_PAYLOAD";

	private static final String BROKER = "tcp://192.168.43.147:1883";
	private static final String CLIENT_NAME = "headunit";
	

	private MqttClient mqttClient;
	private String data;

	private ArrayList<Callback> callbacks = new ArrayList<Callback>();
	private Thread mqttClientThread;

	public interface Callback {
		public void onMessage(String topic, String payload);
		public void onConnected();
	}

	public MqttWorker(Callback callback) {
		callbacks.add(callback);
	}

	/**
	 * Connects to the MQTT broker in a new Thread and defines some custom
	 * callback that handle messages.
	 */
	public void connect() {
		if (mqttClientThread != null && mqttClientThread.isAlive()) {
			try {
				mqttClient.disconnect();
				mqttClient.connect();
			} catch (MqttException e) {
				e.printStackTrace();
			}
		} else {
			mqttClientThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						// Sets up the client and subscribes to topics
						String tmpDir = Environment.getExternalStorageDirectory() + STORAGE_DIRECTORY;
						MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
						mqttClient = new MqttClient(BROKER, CLIENT_NAME, dataStore);
						mqttClient.setCallback(new CustomMqttCallback());
						mqttClient.connect();
						mqttClient.subscribe(TOPIC_SYSTEM);
						notifyOnConnected();
						Log.d(SERVICE_NAME, "subscribing to system");
					} catch (MqttException e) {
						e.printStackTrace();
					}
				}
				
				private void notifyOnConnected() {
					for(Callback callback : callbacks) {
						callback.onConnected();
					}
				}

				/**
				 * Called when messages are received. Filters out data from
				 * installation messages since it is too much to pass around as
				 * Strings.
				 */
				class CustomMqttCallback implements MqttCallback {

					private static final String ACTION_GET_METHOD = "getData";

					@Override
					public void messageArrived(MqttTopic topic, MqttMessage message) {	
						JSONObject json;
						String payload = "";
						String stringTopic = "";
						try {
							json = new JSONObject(message.toString());
							stringTopic = topic.toString();
							// Filter install messages and their data separately
							if (json.getString(ACTION).equals(ACTION_INSTALL)) {
								data = json.getString(ACTION_DATA);
								json = new JSONObject();
								json.put(ACTION, ACTION_INSTALL);
								json.put(ACTION_DATA, ACTION_GET_METHOD);
								payload = json.toString();
							} else {
								payload = message.toString();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

						Log.d(SERVICE_NAME, "messageArrived" + "topic:" + stringTopic + ", message:" + payload);
						notifyCallbacks(stringTopic, payload);
					}

					private void notifyCallbacks(String stringTopic, String payload) {
						for (Callback callback : callbacks) {
							callback.onMessage(stringTopic, payload);
						}
					}

					@Override
					public void deliveryComplete(MqttDeliveryToken token) {
						Log.d(SERVICE_NAME, "deliveryComplete " + "token:" + token);
					}

					@Override
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
	private void disconnect() {
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
			mqttClient.getTopic(topic).publish(payload);
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

}