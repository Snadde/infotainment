package se.chalmers.pd.sensors;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This controller class creates messages and asks a mqtt worker to publish
 * them. The worker is instantiated when the object is created and the thread is
 * started. There is no guarantee however that the thread will start and connect
 * immediately.
 */
public class Controller {

	private static final String TOPIC = "/sensor/infotainment";
	private static final String ACTION = "action";
	private static final String ACTION_NEXT = "next";
	private static final String ACTION_PLAY = "play";
	private static final String ACTION_PAUSE = "pause";

	private MqttWorker mqttWorker;

	/**
	 * Creates a an mqtt worker and tells it to start
	 */
	public Controller() {
		mqttWorker = new MqttWorker();
		mqttWorker.start();
	}

	/**
	 * Tells the worker to publish a message on this specific sensor topic with
	 * a play message.
	 */
	public void play() {
		mqttWorker.publish(TOPIC, getJsonMessage(ACTION_PLAY));
	}

	/**
	 * Tells the worker to publish a message on this specific sensor topic with
	 * a pause message.
	 */
	public void pause() {
		mqttWorker.publish(TOPIC, getJsonMessage(ACTION_PAUSE));
	}

	/**
	 * Tells the worker to publish a message on this specific sensor topic with
	 * a next message.
	 */
	public void next() {
		mqttWorker.publish(TOPIC, getJsonMessage(ACTION_NEXT));
	}

	/**
	 * Helper method to create a json object that can be stringified and sent to
	 * the receiver.
	 * 
	 * @param action
	 *            the action that the message will contain
	 * @return a stringified json object containing the action that is passed
	 *         in.
	 */
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
