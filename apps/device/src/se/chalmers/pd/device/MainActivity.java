package se.chalmers.pd.device;

import se.chalmers.pd.device.ApplicationController.Callbacks;
import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * This Class is separated from all logic and consist only of the view part
 * of the application. It sets up all the buttons and adds onclicklisteners.
 * 
 * @author Patrik Thituson
 *
 */
public class MainActivity extends Activity implements Callbacks, View.OnClickListener {

	private ApplicationController controller;
	private TextView status;
	ImageButton previous, play, next, pause;
	Button connect, install, uninstall, start, stop, disconnect;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		status = (TextView) findViewById(R.id.status);
		status.setMovementMethod(new ScrollingMovementMethod());
		controller = new ApplicationController(this);
		setupButtons();
	}

	/**
	 * Sets up all the buttons for the view and adds onclick listeners.
	 */
	private void setupButtons() {
		connect = (Button) findViewById(R.id.connect);
		install = (Button) findViewById(R.id.install);
		uninstall = (Button) findViewById(R.id.uninstall);
		start = (Button) findViewById(R.id.start);
		stop = (Button) findViewById(R.id.stop);
		disconnect = (Button) findViewById(R.id.disconnect);
		previous = (ImageButton) findViewById(R.id.prev);
		next = (ImageButton) findViewById(R.id.next);
		play = (ImageButton) findViewById(R.id.play);
		pause = (ImageButton) findViewById(R.id.pause);
		
		connect.setOnClickListener(this);
		install.setOnClickListener(this);
		uninstall.setOnClickListener(this);
		start.setOnClickListener(this);
		stop.setOnClickListener(this);
		disconnect.setOnClickListener(this);
		previous.setOnClickListener(this); 
		next.setOnClickListener(this);
		play.setOnClickListener(this);
		pause.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	/**
	 * Calls the controllers onDestroy to prevent crash in
	 *  other threads
	 */
	@Override
	protected void onDestroy() {
		controller.onDestroy();
		super.onDestroy();

	}
	
	/**
	 * Sets the text of stats view
	 * @param text
	 */
	public void setText(String text) {
		status.setText(text);
	}
	
	/**
	 * Helper method that makes the Player buttons visible
	 * called when user is loged in.
	 */
	private void showPlayerButtons() {
			
		previous.setVisibility(View.VISIBLE);
		play.setVisibility(View.VISIBLE);
		next.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Callback that is called when the user has successfully
	 * logged in
	 */
	public void onPlayerLoggedIn() {
		showPlayerButtons();
	}
	
	/**
	 * Callback that is called when the Player has started playing
	 */
	public void onPlayerPlay() {
		play.setVisibility(View.GONE);
		pause.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Callback that is called when the Player has paused playing
	 */
	public void onPlayerPause() {
		play.setVisibility(View.VISIBLE);
		pause.setVisibility(View.GONE);
	}
	
	/**
	 * Callback that is called when the Web application has started
	 * successfully or stopped. The parameter 'show' will decide whether to show
	 * or hide the buttons
	 */
	public void onStartedApplication(final boolean show) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (show) {
					stop.setVisibility(View.VISIBLE);
					start.setVisibility(View.GONE);
					uninstall.setVisibility(View.GONE);
				} else {
					stop.setVisibility(View.GONE);
					start.setVisibility(View.VISIBLE);
					uninstall.setVisibility(View.VISIBLE);
				}
			}
		});

	}
	/**
	 * Callback that is called when we want to change the visibility of the 
	 * buttons start, uninstall and install. 
	 */
	public void onInstalledApplication(final boolean show) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (show) {
					start.setVisibility(View.VISIBLE);
					uninstall.setVisibility(View.VISIBLE);
					install.setVisibility(View.GONE);
				} else {
					start.setVisibility(View.GONE);
					uninstall.setVisibility(View.GONE);
					install.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	
	/**
	 * Callback that is called when the application is connected with the broker
	 */
	public void onConnectedMQTT() {
		runOnUiThread(new Runnable() {
			public void run() {
				connect.setVisibility(View.GONE);
				disconnect.setVisibility(View.VISIBLE);
			}

		});
	}
	
	/**
	 * The onclick method will check which button was clicked and forward this to the 
	 * controller.
	 */
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.connect:
			controller.connect();
			break;
		case R.id.disconnect:
			controller.disconnect();
			break;
		case R.id.install:
			controller.install();
			setText("Installing from device");
			break;
		case R.id.uninstall:
			controller.uninstall();
			break;
		case R.id.start:
			controller.start();
			controller.login();
			break;
		case R.id.stop:
			controller.stop();
			break;
		case R.id.play:
			controller.play();
			break;
		case R.id.next:
			controller.next();
			break;
		case R.id.pause:
			controller.pause();
			break;
		case R.id.prev:
			controller.previous();
			break;
		}
		
	}
}