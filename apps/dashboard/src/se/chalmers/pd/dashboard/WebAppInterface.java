package se.chalmers.pd.dashboard;

import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * This is a JavaScript interface that can be used by a web view. It allows the
 * user to handle the basic MQTT events. The class requires an
 * ApplicationController since it is used for callbacks and control of the
 * application.
 */
public class WebAppInterface {
	ApplicationController controller;

	WebAppInterface(ApplicationController controller) {
		this.controller = controller;
	}

	/**
	 * Publishes an MQTT message
	 * 
	 * @param topic
	 *            a string topic
	 * @param message
	 *            a stringified JSON object
	 */
	@JavascriptInterface
	public void publish(String topic, String message) {
		Log.d("WebAppInterface", "publishing to " + topic + " with message " + message);
		controller.publish(topic, message);
	}

	/**
	 * Subscribes to a specific topic
	 * 
	 * @param topic
	 */
	@JavascriptInterface
	public void subscribe(String topic) {
		Log.d("WebAppInterface", "subscribing to " + topic);
		controller.subscribe(topic);
	}

	/**
	 * Unsubscribes from a specific topic
	 * 
	 * @param topic
	 */
	@JavascriptInterface
	public void unsubscribe(String topic) {
		Log.d("WebAppInterface", "unsubscribing to " + topic);
		controller.unsubscribe(topic);
	}

	/**
	 * Shows a short toast to the user
	 * 
	 * @param message
	 */
	@JavascriptInterface
	public void showToast(String message) {
		controller.showToast(message);
	}
}
