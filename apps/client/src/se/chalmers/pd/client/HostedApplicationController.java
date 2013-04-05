package se.chalmers.pd.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HostedApplicationController implements MqttBroadcastReceiver.Callbacks {

	private final String DEFAULT_URL = "file:///android_asset/index.html";
	private final String BASEDIR = Environment.getExternalStorageDirectory() + "/infotainment/apps/";
	private WebView webView;
	private Context context;

	public HostedApplicationController(WebView webView, Context context) {
		this.webView = webView;
		this.context = context;
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webView.setWebViewClient(new CustomWebViewClient());
		webView.setWebChromeClient(new CustomWebChromeClient());
		webView.loadUrl(DEFAULT_URL);
	}

	public boolean init(String appName) {
		File directory = new File(BASEDIR + appName);
		if (directory.exists() && directory.isDirectory()) {
			Log.d("HostedApplicationController", "init " + appName + " exists");
			return true;
		}
		Log.d("HostedApplicationController", "init " + appName + " doesn't exist or is not a directory");
		return false;
	}

	public boolean install(InputStream inputStream) {
		String unzipLocation = BASEDIR;
		Decompresser decompresser = new Decompresser(unzipLocation);
		if (decompresser.unzip(inputStream)) {
			return true;
		}
		return false;
	}

	public void start(String location) {
		webView.loadUrl("javascript:loadApp('" + "file://" + BASEDIR + location + "')");
	}

	public void stop() {
		webView.loadUrl(DEFAULT_URL);
	}

	public void uninstall(String appName) {
		Log.d("HostedApplicationController", "uninstall " + appName);
		File directory = new File(BASEDIR + appName);
		if (directory.exists() && directory.isDirectory()) {
			deleteRecursive(directory);
		}
	}

	private void deleteRecursive(File appDir) {
		if (appDir.isDirectory()) {
			for (File child : appDir.listFiles()) {
				deleteRecursive(child);
			}
		}
		appDir.delete();
	}

	private class CustomWebChromeClient extends WebChromeClient {
		public boolean onConsoleMessage(ConsoleMessage cm) {
			Log.d("CustomWebChromeClient", cm.message() + " -- From line " + cm.lineNumber() + " of " + cm.sourceId());
			return true;
		}
	}

	private class CustomWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return false;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			onLoadComplete(url);
			Log.d("CustomWebViewClient", "onPageFinished " + url);
		}
	}
	
	@Override
	public void onMessageReceived(String topic, String payload) {
		Log.d("CustomWebViewClient", "onMessage " + "topic: " + topic + ", payload: " + payload);
	}

	@Override
	public void onStatusUpdate(String status) {
		Log.d("CustomWebViewClient", "onMessage " + "status: " + status);
	}
	
	public void onLoadComplete(String url) {
		if (init("webapp")) {
			// start("/webapp/index.html");
			// uninstall("webapp");
		} else {
			// install(getInputStream());
		}
	}

	private InputStream getInputStream() {
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
