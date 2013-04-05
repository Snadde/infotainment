package se.chalmers.pd.client;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		setupView();
	}

	private void setupView() {
		WebView webView = (WebView) findViewById(R.id.webview);
		HostedApplicationController controller= new HostedApplicationController(webView, this);
		MqttBroadcastReceiver receiver = new MqttBroadcastReceiver(controller);
		IntentFilter messageReceivedFilter = new IntentFilter(MQTTService.MQTT_MSG_RECEIVED_INTENT);
		IntentFilter statusFilter = new IntentFilter(MQTTService.MQTT_STATUS_INTENT);
		registerReceiver(receiver, messageReceivedFilter);
		registerReceiver(receiver, statusFilter);
		
		Intent service = new Intent(this, MQTTService.class);
		startService(service); 
	}
}
