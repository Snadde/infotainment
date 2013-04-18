package se.chalmers.pd.dashboard;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class WebAppInterface {
	ApplicationController controller;

    WebAppInterface(ApplicationController controller) {
        this.controller = controller;
    }
   
    @JavascriptInterface
    public void publish(String topic, String message) {
    	Log.d("WebAppInterface", "pub");
        controller.publish(topic, message);
    }
}
