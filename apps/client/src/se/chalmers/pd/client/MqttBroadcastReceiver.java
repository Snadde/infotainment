package se.chalmers.pd.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * This broadcast receiver listens for messages from the filters defined in the
 * broadcasting class.
 *
 */
public class MqttBroadcastReceiver extends BroadcastReceiver {

	public interface Callbacks {
		public void onMessageReceived(String topic, String payload);
		public void onStatusUpdate(String status);
	}

	private Callbacks callback;
	
	public MqttBroadcastReceiver(Callbacks callback) {
		this.callback = callback;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		String action = intent.getAction();
		
		if(action.equals(MQTTService.MQTT_MESSAGE_RECEIVED_INTENT)) {
			String topic = extras.getString(MQTTService.MQTT_MESSAGE_RECEIVED_TOPIC);
			String payload = extras.getString(MQTTService.MQTT_MESSAGE_RECEIVED_PAYLOAD);
			callback.onMessageReceived(topic, payload);
		} else if(action.equals(MQTTService.MQTT_STATUS_INTENT)) {
			String status = extras.getString(MQTTService.MQTT_STATUS_MESSAGE);
			callback.onStatusUpdate(status);
		}
	}
}
