package se.chalmers.pd.device;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

public class ApplicationController implements MqttBroadcastReceiver.Callbacks {
	private Context context;
	private MQTTService mqttService;
	private boolean debug = true;
	private MainActivity mainActivity;

	public ApplicationController(MQTTService mqttService, Context context) {
		this.mqttService = mqttService;
		this.context = context;
		mainActivity = (MainActivity) context;
		setupReceivers();
	}

	/**
	 * Creates the broadcast receiver and registers it with the custom intent
	 * filters defined in the service.
	 */
	private void setupReceivers() {
		MqttBroadcastReceiver receiver = new MqttBroadcastReceiver(this);
		IntentFilter messageReceivedFilter = new IntentFilter(MQTTService.MQTT_MESSAGE_RECEIVED_INTENT);
		IntentFilter statusFilter = new IntentFilter(MQTTService.MQTT_STATUS_INTENT);
		context.registerReceiver(receiver, messageReceivedFilter);
		context.registerReceiver(receiver, statusFilter);
	}

	public void onMessageReceived(String topic, String payload) {
		log("Message Received --> \n",  "\t Topic: " + topic + "\n \t Payload: " + payload);
	}

	public void onStatusUpdate(String status) {
		log("ApplicationController", "onStatusUpdate " + "status: " + status);
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
		log("ApplicationController", "publish " + "topic: " + topic + " payload: " + payload);
		if(mqttService != null)
			mqttService.publish(topic, payload);
	}
	
	/**
	 * Helper method for logging
	 * 
	 * @param tag
	 *            to log as
	 * @param message
	 *            to log
	 */
	private void log(String tag, final String message) {
		if (debug ) {
			Log.d(tag, message);
			((MainActivity) context).runOnUiThread(new Runnable() {
				public void run() {
					mainActivity.setText(message);
				}
			});
		}
	}
	
	/**
	 * Forwards subscribe request to the mqttservice
	 * 
	 * @param topic
	 */
	public void subscribe(String topic) {
		mqttService.subscribe(topic);
	}
	
	/**
	 * Forwards unsubscribe request to the mqttservice
	 * 
	 * @param topic
	 */
	public void unsubscribe(String topic) {
		mqttService.unsubscribe(topic);
	}
	/**
	 * Disconnects from the mqtt broker
	 */
	public void disconnect(){
		mqttService.disconnect();
	}

}
