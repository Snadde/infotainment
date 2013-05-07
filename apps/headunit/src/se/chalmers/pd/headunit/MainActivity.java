package se.chalmers.pd.headunit;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Main class which contains the WebView and hosts the application controller. This class
 * sets up the basic webview settings and starts the MqttController which contains the MQTT
 * client who communicates with the controller.
 */
public class MainActivity extends Activity {

	private static final String JAVASCRIPT_INTERFACE = "WebApp";
	private static final String STORAGE_FOLDER = "/infotainment/";
	private WebView webView;
	private ApplicationController controller;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//        .detectAll()
//        .penaltyLog()
//        .build());
//		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		webView = (WebView) findViewById(R.id.webview);
		
		// Create the app control
		controller = new ApplicationController(webView, this);
        
		// Setup the webview and the settings we need
        webView.addJavascriptInterface(new WebAppInterface(controller), JAVASCRIPT_INTERFACE);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setGeolocationDatabasePath(Environment.getExternalStorageDirectory() + STORAGE_FOLDER);
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		webView.setWebViewClient(new CustomWebViewClient());
		webView.setWebChromeClient(new CustomWebChromeClient());
		
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		
		controller.connect();
	}

	/**
	 * A custom webchromeclient for the webview that hijacks that logging from the console and 
	 * adds it to logcat. Also auto-accepts any location requests from the webview.
	 */
	private class CustomWebChromeClient extends WebChromeClient {
		public boolean onConsoleMessage(ConsoleMessage cm) {
			Log.d("CustomWebChromeClient", cm.message() + " -- From line " + cm.lineNumber() + " of " + cm.sourceId());
			return true;
		}
		
		public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
		    callback.invoke(origin, true, false);
		 }
	}

	/**
	 * Custom webviewclient that makes sure all URL's are opened by this specific webview
	 * and overrides any other intents.
	 */
	private class CustomWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return false;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			controller.onLoadComplete(url);
			Log.d("CustomWebViewClient", "onPageFinished " + url);
		}
	}
}
