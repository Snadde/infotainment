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

	private void setupButtons() {

		connect = (Button) findViewById(R.id.connect);
		install = (Button) findViewById(R.id.install);
		uninstall = (Button) findViewById(R.id.uninstall);
		start = (Button) findViewById(R.id.start);
		stop = (Button) findViewById(R.id.stop);
		disconnect = (Button) findViewById(R.id.disconnect);
		connect.setOnClickListener(this);
		install.setOnClickListener(this);
		uninstall.setOnClickListener(this);
		start.setOnClickListener(this);
		stop.setOnClickListener(this);
		disconnect.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		controller.onDestroy();
		super.onDestroy();

	}

	public void setText(String text) {
		status.setText(text);
	}

	private void setupPlayerButtons() {
		previous = (ImageButton) findViewById(R.id.prev);
		next = (ImageButton) findViewById(R.id.next);
		play = (ImageButton) findViewById(R.id.play);
		pause = (ImageButton) findViewById(R.id.pause);
		
		previous.setOnClickListener(this); 
		next.setOnClickListener(this);
		play.setOnClickListener(this);
		pause.setOnClickListener(this);
		
		previous.setVisibility(View.VISIBLE);
		play.setVisibility(View.VISIBLE);
		next.setVisibility(View.VISIBLE);
	}

	public void onPlayerLoggedIn() {
		setupPlayerButtons();
	}

	public void onPlayerPlay() {
		play.setVisibility(View.GONE);
		pause.setVisibility(View.VISIBLE);
	}

	public void onPlayerPause() {
		play.setVisibility(View.VISIBLE);
		pause.setVisibility(View.GONE);
	}

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

	public void onConnectedMQTT() {
		runOnUiThread(new Runnable() {
			public void run() {
				connect.setVisibility(View.GONE);
				disconnect.setVisibility(View.VISIBLE);
			}

		});
	}

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