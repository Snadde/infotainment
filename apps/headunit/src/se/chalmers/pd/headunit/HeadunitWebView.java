package se.chalmers.pd.headunit;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.*;


public class HeadunitWebView extends WebView {

    private static final String JAVASCRIPT_INTERFACE = "WebApp";
    private static final String STORAGE_FOLDER = "/infotainment/";
    private ApplicationController controller;

    public HeadunitWebView(Context context) {
        super(context);
    }

    public HeadunitWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeadunitWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HeadunitWebView(Context context, AttributeSet attrs, int defStyle, boolean privateBrowsing) {
        super(context, attrs, defStyle, privateBrowsing);
    }


    public void setup(ApplicationController controller) {
        this.controller = controller;
        // Setup the webview and the settings we need
        this.addJavascriptInterface(new WebAppInterface(controller), JAVASCRIPT_INTERFACE);
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationDatabasePath(Environment.getExternalStorageDirectory() + STORAGE_FOLDER);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        this.setFocusableInTouchMode(false);
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        this.setWebViewClient(new CustomWebViewClient());
        this.setWebChromeClient(new CustomWebChromeClient());
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
