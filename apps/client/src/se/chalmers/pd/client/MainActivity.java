package se.chalmers.pd.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

public class MainActivity extends Activity implements HostedApplicationController.Callbacks {

	private WebView webView;
	private HostedApplicationController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		setupView();
	}

	private void setupView() {
		webView = (WebView) findViewById(R.id.webview);
		controller = new HostedApplicationController(webView, this);
	}

	@Override
	public void onLoadComplete(String url) {
		if(controller.init("webapp")) {
			//controller.start("/webapp/index.html");
			controller.uninstall("webapp");
		} else {
			//controller.install();
		}
	}
}
