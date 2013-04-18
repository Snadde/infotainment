package se.chalmers.pd.dashboard;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * This class handles the control flow of the application. It listens for
 * broadcasts by implementing the callbacks interface in a
 * MqttBroadcastReceiver. The messages received from the receiver are parsed and
 * passed on as json to the container web application that is running in the
 * webview.
 */
public class ApplicationController implements MqttBroadcastReceiver.Callbacks, Decompresser.Callbacks {

	private final String DEFAULT_URL = "file:///android_asset/index.html";
	private final String BASEDIR = Environment.getExternalStorageDirectory() + "/infotainment/apps/";

	private WebView webView;
	private Context context;
	private MQTTService mqttService;
	private TextView statusView;
	private boolean debug = true;

	/**
	 * Sets up the receivers and initiates the object.
	 * 
	 * @param webView
	 * @param context
	 * @param mainActivity
	 */
	public ApplicationController(WebView webView, MQTTService mqttService, Context context) {
		this.webView = webView;
		this.mqttService = mqttService;
		this.context = context;
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

	/**
	 * Checks if the application exists in the app folder.
	 * 
	 * @param appName
	 * @return true if it does exist
	 */
	public boolean applicationExists(String appName) {
		File directory = new File(BASEDIR + "/" + appName);
		if (directory.exists() && directory.isDirectory()) {
			log("ApplicationController", "init " + appName + " exists");
			return true;
		}
		log("ApplicationController", "init " + appName + " doesn't exist or is not a directory");
		return false;
	}

	/**
	 * Unzips the data in the inputstream and places it in the app folder.
	 * 
	 * @param inputStream
	 *            (from the zip file)
	 * @param privateTopic 
	 * @return true if successful
	 */
	public void install(InputStream inputStream, String privateTopic) {
		String unzipLocation = BASEDIR;
		Decompresser decompresser = new Decompresser(unzipLocation, this, privateTopic);
		decompresser.unzip(inputStream);
	}

	/**
	 * Loads the app at the given location which is the same as the app name
	 * into the web view.
	 * 
	 * @param appName
	 *            the app name (folder name of application)
	 */
	public boolean start(String appName) {
		String url = "file://" + BASEDIR + appName + "/index.html";
		webView.loadUrl(url);
		log("ApplicationController", "start " + url);
		return true;
	}

	/**
	 * Stops the currently running application in the webview by loading the
	 * default url.
	 */
	public void stop() {
		webView.loadUrl(DEFAULT_URL);
	}

	/**
	 * Uninstalls the application with the given app name. Note that the app
	 * name needs to be identical to the foldername of the install.
	 * 
	 * @param appName
	 */
	public void uninstall(String appName) {
		log("ApplicationController", "uninstall " + appName);
		File directory = new File(BASEDIR + appName);
		if (directory.exists() && directory.isDirectory()) {
			deleteRecursive(directory);
		}
		// TODO Confirm uninstall complete
	}

	/**
	 * Deletes all files and folders recursively from appDir.
	 * 
	 * @param appDir
	 */
	private void deleteRecursive(File appDir) {
		if (appDir.isDirectory()) {
			for (File child : appDir.listFiles()) {
				deleteRecursive(child);
			}
		}
		log("ApplicationController", "deleteRecursive " + appDir.getAbsolutePath());
		appDir.delete();
	}

	@Override
	public void onMessageReceived(String topic, String payload) {
		log("ApplicationController", "onMessageReceived " + "topic: " + topic);
		if (topic.equals("/system")) {
			handleSystemMessage(payload);
		} else {
			handleMessage(topic, payload);
		}
	}

	private void handleSystemMessage(String payload) {
		try {
			JSONObject responsePayload = new JSONObject();
			JSONObject json = new JSONObject(payload);
			String action = json.getString("action");
			String data = json.getString("data");
			String privateTopic = "/app/webapp/1";

			// TODO Add checking to make sure that action is one of a well defined enum or array
			responsePayload.put("action", action);

			if (action.equals("exist")) {
				// mqttService.subscribe("/app/webapp");
				if (applicationExists(data)) {
					responsePayload.put("data", "success");
				} else {
					responsePayload.put("data", "error");
					responsePayload.put("error", "Application does not exist, payload was: " + payload);
				}
			} else if (action.equals("install")) {
				install(getInputStream(data), privateTopic);
			} else if (action.equals("start")) {
				if (start(data)) {
					responsePayload.put("data", "success");
				} else {
					responsePayload.put("data", "error");
					responsePayload.put("error", "Could not start the application, payload was: " + payload);
				}
			} else if (action.equals("stop")) {
				webView.loadUrl(DEFAULT_URL);
				responsePayload.put("data", "success");
			} else if (action.equals("uninstall")) {
				uninstall(data);
				responsePayload.put("data", "success");
			}

			sendResponse(privateTopic, responsePayload);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void handleMessage(String topic, String payload) {
		webView.loadUrl("javascript:onMessage(" + payload + ")");
	}

	private void sendResponse(String topic, JSONObject responsePayload) throws JSONException {
		responsePayload.put("type", "response");
		mqttService.publish(topic, responsePayload.toString());
	}

	@Override
	public void onStatusUpdate(String status) {
		log("ApplicationController", "onStatusUpdate " + "status: " + status);
	}

	// public void onLoadComplete(String url) {
	// if (init("webapp")) {
	// // start("/webapp/index.html");
	// // uninstall("webapp");
	// } else {
	// // install(getInputStream());
	// }
	// }

	private InputStream getInputStream(String data) {
		InputStream inputStream = (InputStream) new ByteArrayInputStream(Base64.decode(data, Base64.DEFAULT));
		return inputStream;
	}

	public void publish(String topic, String message) {
		log("ApplicationController", "publish " + "topic: " + topic + " message: " + message);
		mqttService.publish(topic, message);
	}

	@Override
	public void decompressComplete(boolean result, String privateTopic) {
		JSONObject responsePayload = new JSONObject();
		try {
			responsePayload.put("action", "install");
			if (result) {
				responsePayload.put("data", "success");
				log("decompressComplete", "installation complete");
			} else {
				responsePayload.put("data", "error");
				responsePayload.put("error", "Could not install the application.");
				log("decompressComplete", "failed to install");
			}
			sendResponse(privateTopic, responsePayload);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void setStatusView(TextView view) {
		statusView = view;
	}
	
	private void log(String tag, final String message) {
		if(debug) {
			Log.d(tag, message);
			((MainActivity) context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					statusView.setText(message);
				}
			});
		}
	}

}
