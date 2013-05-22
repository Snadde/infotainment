package se.chalmers.pd.headunit;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
 * sets up the special webview settings and starts the MqttController which contains the MQTT
 * client who communicates with the controller.
 */
public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
        HeadunitWebView webView = (HeadunitWebView) findViewById(R.id.webview);
        ApplicationController controller = new ApplicationController(webView, this);
		webView.setup(controller);
		controller.reconnect();
	}

}
