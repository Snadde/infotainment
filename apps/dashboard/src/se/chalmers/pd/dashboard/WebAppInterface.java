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
    	Log.d("WebAppInterface", "publishing to " + topic + " with message " + message);
        controller.publish(topic, message);
    }
    
    @JavascriptInterface
    public void subscribe(String topic) {
    	Log.d("WebAppInterface", "subscribing to " + topic);
        controller.subscribe(topic);
    }
    
    @JavascriptInterface
    public void unsubscribe(String topic) {
    	Log.d("WebAppInterface", "unsubscribing to " + topic);
        controller.unsubscribe(topic);
    }
    
    @JavascriptInterface
    public void showToast(String message) {
        controller.showToast(message);
    }
}
