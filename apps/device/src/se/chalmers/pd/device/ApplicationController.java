package se.chalmers.pd.device;

import org.json.JSONException;
import org.json.JSONObject;

import se.chalmers.pd.device.MqttWorker.Callback;
import android.content.Context;
import android.util.Log;

public class ApplicationController implements Callback {
	private Context context;
	private MqttWorker mqttService;
	private boolean debug = true;
	private MainActivity mainActivity;

	public ApplicationController(Context context) {
		this.mqttService = new MqttWorker(this);
		this.context = context;
		mainActivity = (MainActivity) context;
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
		log("ApplicationController", "publish " + "topic: " + topic + " payload: ");
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
	
	/**
	 * Disconnects from the mqtt broker
	 */
	public void connect(){
		mqttService.connect();
	}
	

	public void onMessage(String topic, String payload) {
		try {
			JSONObject responsePayload = new JSONObject();
			JSONObject json = new JSONObject(payload);
			String action = json.getString(MqttWorker.ACTION);
			String data = json.getString(MqttWorker.ACTION_DATA);
			mainActivity.setText(action + " " +  data);
			//switch(action){
			//case MqttWorker.ACTION_INSTALL:
				
			//	break;
		//}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
