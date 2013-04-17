package se.chalmers.pd.dashboard;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Main class which contains the WebView and hosts the application controller. This class
 * sets up the basic webview settings and starts the MQTTService which contains the MQTT
 * client who communicates with the controller.
 */
public class MainActivity extends Activity {

	private WebView webView;
	private ApplicationController controller;
	private MQTTService mqttService;
	private boolean isBound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		bindService();
		
		webView = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webView.setWebViewClient(new CustomWebViewClient());
		webView.setWebChromeClient(new CustomWebChromeClient());
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        mqttService = ((MQTTService.LocalBinder)service).getService();
	        controller = new ApplicationController(webView, mqttService, MainActivity.this);
	        webView.addJavascriptInterface(new WebAppInterface(controller), "WebApp");
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        mqttService = null;
	    }
	};

	private void bindService() {
		Intent intent = new Intent(MainActivity.this, MQTTService.class);
	    bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	    startService(intent);
	    isBound = true;
	}

	private void unbindService() {
	    if (isBound) {
	        unbindService(serviceConnection);
	        isBound = false;
	    }
	}

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    unbindService();
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
			//controller.onLoadComplete(url);
			Log.d("CustomWebViewClient", "onPageFinished " + url);
		}
	}
}
