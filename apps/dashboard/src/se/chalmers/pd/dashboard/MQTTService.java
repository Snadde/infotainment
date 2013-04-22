package se.chalmers.pd.dashboard;

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

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

/**
 * This service launches an MQTT client in a separate thread and subscribes to the
 * systems basic topics. When a message is received, it broadcasts the message using
 * custom intent filters.
 *
 */
public class MQTTService extends Service {
	
	private static final String STORAGE_DIRECTORY = "/infotainment/";
	private static final String SERVICE_NAME = "MQTTService";
	
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
	
	/**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
    	MQTTService getService() {
            return MQTTService.this;
        }
    }
    
    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder binder = new LocalBinder();


	public static final String MQTT_STATUS_INTENT = "se.chalmers.pd.dashboard.mqtt.STATUS";
	public static final String MQTT_STATUS_MESSAGE = "se.chalmers.pd.dashboard.mqtt.STATUS_MESSAGE";
	public static final String MQTT_MESSAGE_RECEIVED_INTENT = "se.chalmers.pd.dashboard.mqtt.MESSAGE_RECEIVED";
    public static final String MQTT_MESSAGE_RECEIVED_TOPIC = "se.chalmers.pd.dashboard.mqtt.MESSAGE_RECEIVED_TOPIC";
    public static final String MQTT_MESSAGE_RECEIVED_PAYLOAD = "se.chalmers.pd.dashboard.mqtt.MESSAGE_RECEIVED_PAYLOAD";
    
	private static final String BROKER = "tcp://192.168.43.147:1883";
	private static final String CLIENT_NAME = "dashboard";
	
	private MqttClient mqttClient;
	

	@Override
	public void onCreate() {

	}

	@Override
	public void onStart(final Intent intent, final int startId) {
		connect();
	}

	@Override
	public int onStartCommand(final Intent intent, int flags, final int startId) {
		connect();
		return START_STICKY;
	}

	private String data;
	
	private void connect() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String tmpDir = Environment.getExternalStorageDirectory() + STORAGE_DIRECTORY;
					MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
					mqttClient = new MqttClient(BROKER, CLIENT_NAME, dataStore);
					mqttClient.setCallback(new CustomMqttCallback());
					mqttClient.connect();
					mqttClient.subscribe(TOPIC_SYSTEM);
					mqttClient.subscribe("/app/webapp");
					Log.d(SERVICE_NAME, "subscribing");
				} catch (MqttException e) {
					e.printStackTrace();
				}
			}
			
			class CustomMqttCallback implements MqttCallback {

				private static final String ACTION_GET_METHOD = "getData";

				@Override
				public void messageArrived(MqttTopic topic, MqttMessage message) throws Exception {
					JSONObject json = new JSONObject(message.toString());
					String payload = "";
					String stringTopic = topic.toString();
					if(json.getString(ACTION).equals(ACTION_INSTALL)) {
						data = json.getString(ACTION_DATA);
						json = new JSONObject();
						json.put(ACTION, ACTION_INSTALL);
						json.put(ACTION_DATA, ACTION_GET_METHOD);
						payload = json.toString();
					} else {
						payload = message.toString();
					}
					broadcastReceivedMessage(stringTopic, payload);
					broadcastServiceStatus("messageArrived");
					Log.d(SERVICE_NAME, "messageArrived " + "topic:" + stringTopic + ", message:" + payload);
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
		}, SERVICE_NAME).start();
	}

	@Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
		disconnect();
		broadcastServiceStatus("Disconnected");
	}

	private void disconnect() {
		try {
			mqttClient.disconnect();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	private void broadcastServiceStatus(String statusDescription) {
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(MQTT_STATUS_INTENT);
		broadcastIntent.putExtra(MQTT_STATUS_MESSAGE, statusDescription);
		sendBroadcast(broadcastIntent);
	}

	private void broadcastReceivedMessage(String topic, String message) {
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(MQTT_MESSAGE_RECEIVED_INTENT);
		broadcastIntent.putExtra(MQTT_MESSAGE_RECEIVED_TOPIC, topic);
		broadcastIntent.putExtra(MQTT_MESSAGE_RECEIVED_PAYLOAD, message);
		sendBroadcast(broadcastIntent);
	}

	public void publish(String topic, String message) {
		try {
			MqttMessage payload = new MqttMessage(message.getBytes());
			mqttClient.getTopic(topic).publish(payload);
		} catch (MqttPersistenceException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public void subscribe(String topic) {
		try {
			mqttClient.subscribe(topic);
		} catch (MqttSecurityException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	
	public void unsubscribe(String topic) {
		try {
			mqttClient.unsubscribe(topic);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public String getData() {
		return data;
	}

	

}