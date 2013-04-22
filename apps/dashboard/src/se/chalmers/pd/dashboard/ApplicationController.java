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
import android.widget.Toast;

/**
 * This class handles the control flow of the application. It listens for
 * broadcasts by implementing the callbacks interface in a
 * MqttBroadcastReceiver. The messages received from the receiver are parsed and
 * passed on as json to the container web application that is running in the
 * webview.
 */
public class ApplicationController implements MqttBroadcastReceiver.Callbacks, Decompresser.Callbacks {

	
	private static final String HTTP_LOCALHOST = "http://localhost:8080/";
	private final String DEFAULT_URL = "file:///android_asset/index.html";
	private final String BASEDIR = Environment.getExternalStorageDirectory() + "/www/";

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
		File directory = new File(BASEDIR + appName);
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
		String url = HTTP_LOCALHOST + appName + "/index.html";
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
		if (topic.equals(MQTTService.TOPIC_SYSTEM)) {
			handleSystemMessage(payload);
		} else {
			handleMessage(topic, payload);
		}
	}

	private void handleSystemMessage(String payload) {
		try {
			JSONObject responsePayload = new JSONObject();
			JSONObject json = new JSONObject(payload);
			String action = json.getString(MQTTService.ACTION);
			String data = json.getString(MQTTService.ACTION_DATA);
			String privateTopic = "/app/webapp/1";

			// TODO Add checking to make sure that action is one of a well defined enum or array
			responsePayload.put(MQTTService.ACTION, action);

			if (action.equals(MQTTService.ACTION_EXIST)) {
				// mqttService.subscribe("/app/webapp");
				if (applicationExists(data)) {
					responsePayload.put(MQTTService.ACTION_DATA, MQTTService.ACTION_SUCCESS);
				} else {
					responsePayload.put(MQTTService.ACTION_DATA, MQTTService.ACTION_ERROR);
					responsePayload.put(MQTTService.ACTION_ERROR, R.string.application_does_not_exist_payload_was + payload);
				}
			} else if (action.equals(MQTTService.ACTION_INSTALL)) {
				data = mqttService.getData();
				install(getInputStream(data), privateTopic);
			} else if (action.equals(MQTTService.ACTION_START)) {
				if (start(data)) {
					responsePayload.put(MQTTService.ACTION_DATA, MQTTService.ACTION_SUCCESS);
				} else {
					responsePayload.put(MQTTService.ACTION_DATA, MQTTService.ACTION_ERROR);
					responsePayload.put(MQTTService.ACTION_ERROR, R.string.could_not_start_the_application_payload_was + payload);
				}
			} else if (action.equals(MQTTService.ACTION_STOP)) {
				webView.loadUrl(DEFAULT_URL);
				responsePayload.put(MQTTService.ACTION_DATA, MQTTService.ACTION_SUCCESS);
			} else if (action.equals(MQTTService.ACTION_UNINSTALL)) {
				uninstall(data);
				responsePayload.put(MQTTService.ACTION_DATA, MQTTService.ACTION_SUCCESS);
			}

			sendResponse(privateTopic, responsePayload);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void handleMessage(String topic, String payload) {
		log("ApplicationController:handleMessage", topic + " " + payload);
		webView.loadUrl("javascript:onMessage('" + topic + "', " + payload + ")");
	}

	private void sendResponse(String topic, JSONObject responsePayload) throws JSONException {
		responsePayload.put(MQTTService.ACTION_TYPE, MQTTService.ACTION_RESPONSE);
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
		log("getInputStream", data); 
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
				responsePayload.put(MQTTService.ACTION_DATA, MQTTService.ACTION_SUCCESS);
				log("decompressComplete", "installation complete");
			} else {
				responsePayload.put(MQTTService.ACTION_DATA, MQTTService.ACTION_ERROR);
				responsePayload.put(MQTTService.ACTION_ERROR, R.string.could_not_install_the_application);
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

	public void subscribe(String topic) {
		mqttService.subscribe(topic);
	}

	public void unsubscribe(String topic) {
		mqttService.unsubscribe(topic);
	}

	public void showToast(String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

}
