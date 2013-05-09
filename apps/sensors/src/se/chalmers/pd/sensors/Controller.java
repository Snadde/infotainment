package se.chalmers.pd.sensors;

import org.json.JSONException;
import org.json.JSONObject;


public class Controller {
	
	private static final String TOPIC = "/sensor/infotainment";
	private static final String ACTION = "action";
	private static final String ACTION_NEXT = "next";
	private static final String ACTION_PLAY = "play";
	private static final String ACTION_PAUSE = "pause";

	private MqttWorker mqttWorker;

	public Controller() {
		mqttWorker = new MqttWorker();
		mqttWorker.start();
	}
	
	public void play() {
		mqttWorker.publish(TOPIC, getJsonMessage(ACTION_PLAY));
	}

	public void pause() {
		mqttWorker.publish(TOPIC, getJsonMessage(ACTION_PAUSE));
	}

	public void next() {
		mqttWorker.publish(TOPIC, getJsonMessage(ACTION_NEXT));
	}
	
	private String getJsonMessage(String action) {
		JSONObject message = new JSONObject();
		try {
			message.put(ACTION, action);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return message.toString();
	}
}
