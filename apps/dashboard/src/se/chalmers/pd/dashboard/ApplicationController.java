package se.chalmers.pd.dashboard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;
import android.webkit.WebView;

/**
 * This class handles the control flow of the application. It listens for
 * broadcasts by implementing the callbacks interface in a
 * MqttBroadcastReceiver. The messages received from the receiver are parsed and
 * passed on as json to the container web application that is running in the
 * webview.
 */
public class ApplicationController implements MqttBroadcastReceiver.Callbacks {

	private final String DEFAULT_URL = "file:///android_asset/index.html";
	private final String BASEDIR = Environment.getExternalStorageDirectory() + "/infotainment/apps/";

	private WebView webView;
	private Context context;

	/**
	 * Sets up the receivers and initiates the object.
	 * 
	 * @param webView
	 * @param context
	 */
	public ApplicationController(WebView webView, Context context) {
		this.webView = webView;
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
	public boolean init(String appName) {
		File directory = new File(BASEDIR + appName);
		if (directory.exists() && directory.isDirectory()) {
			Log.d("ApplicationController", "init " + appName + " exists");
			return true;
		}
		Log.d("ApplicationController", "init " + appName + " doesn't exist or is not a directory");
		return false;
	}

	/**
	 * Unzips the data in the inputstream and places it in the app folder.
	 * 
	 * @param inputStream
	 *            (from the zip file)
	 * @return true if successful
	 */
	public boolean install(InputStream inputStream) {
		String unzipLocation = BASEDIR;
		Decompresser decompresser = new Decompresser(unzipLocation);
		if (decompresser.unzip(inputStream)) {
			return true;
		}
		return false;
	}

	/**
	 * Loads the app at the given location which is the same as the app name
	 * into the web view.
	 * 
	 * @param appName
	 *            the app name (folder name of application)
	 */
	public void start(String appName) {
		String url = "file://" + BASEDIR + appName + "/index.html";
		webView.loadUrl(url);
		Log.d("ApplicationController", "start " + url);
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
		Log.d("ApplicationController", "uninstall " + appName);
		File directory = new File(BASEDIR + appName);
		if (directory.exists() && directory.isDirectory()) {
			deleteRecursive(directory);
		}
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
		Log.d("ApplicationController", "deleteRecursive " + appDir.getAbsolutePath());
		appDir.delete();
	}

	@Override
	public void onMessageReceived(String topic, String payload) {
		Log.d("CustomWebViewClient", "onMessageReceived " + "topic: " + topic + ", payload: " + payload);
		handleMessage(payload);	
	}
	
	private void handleMessage(String payload) {
		try {
			JSONObject json = new JSONObject(payload);
			String action = json.getString("action");
			String data = json.getString("data");
			if(action.equals("install")) {
				install(getInputStream(data));
			} else if(action.equals("start")) {
				start(data);
			} else if(action.equals("stop")) {
				webView.loadUrl(DEFAULT_URL);
			} else if(action.equals("uninstall")) {
				uninstall(data);
			} else {
				webView.loadUrl("javascript:onMessage(" + payload + ")");
			}
			Log.d("CustomWebViewClient", "handleMessage " + " action: " + json.getString("action") + " data: " + json.getString("data"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStatusUpdate(String status) {
		Log.d("CustomWebViewClient", "onStatusUpdate " + "status: " + status);
	}

//	public void onLoadComplete(String url) {
//		if (init("webapp")) {
//			// start("/webapp/index.html");
//			// uninstall("webapp");
//		} else {
//			// install(getInputStream());
//		}
//	}

	private InputStream getInputStream(String data) {
		String zipFilename = "webapp.zip";
		InputStream inputStream = null;
		try {
			inputStream = context.getAssets().open(zipFilename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return inputStream;
	}

}
