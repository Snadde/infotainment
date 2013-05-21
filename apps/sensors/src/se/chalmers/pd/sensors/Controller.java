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
		mqttWorker.publish(TOPIC, getJsonMessage(Action.play));
	}

	/**
	 * Tells the worker to publish a message on this specific sensor topic with
	 * a pause message.
	 */
	public void pause() {
		mqttWorker.publish(TOPIC, getJsonMessage(Action.pause));
	}

	/**
	 * Tells the worker to publish a message on this specific sensor topic with
	 * a next message.
	 */
	public void next() {
		mqttWorker.publish(TOPIC, getJsonMessage(Action.next));
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
	private String getJsonMessage(Action action) {
		JSONObject message = new JSONObject();
		try {
			message.put(Action.action.toString(), action.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return message.toString();
	}
}
