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

public class MainActivity extends Activity implements Callbacks {

	private ApplicationController controller;
	private TextView status;
	ImageButton previous, play, next, pause;
	Button connect, install, exist, uninstall, start, stop, disconnect, installurl;

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
		exist = (Button) findViewById(R.id.exist);
		uninstall = (Button) findViewById(R.id.uninstall);
		start = (Button) findViewById(R.id.start);
		stop = (Button) findViewById(R.id.stop);
		disconnect = (Button) findViewById(R.id.disconnect);
		installurl = (Button) findViewById(R.id.install_url);

		connect.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				controller.connect();
			}
		});

		install.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				install();
			}
		});

		exist.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				exist();
			}
		});

		uninstall.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				uninstall();
			}
		});

		start.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				 start();
				controller.login();

			}

		});

		stop.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				stop();
			}
		});

		disconnect.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				disconnect();
			}
		});

		installurl.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				installURL();
			}
		});

	}

	protected void installURL() {
		String message = DeviceMessage.installFromUrlMessage("playlist");
		controller.publish("/system", message);
		setText("Installing from URL");
	}

	protected void disconnect() {
		controller.disconnect();
		setText("Disconnecting");
	}

	protected void stop() {
		String message = DeviceMessage.stopMessage("playlist");
		controller.publish("/system", message);
		setText("Stopping application");
	}

	protected void start() {
		String message = DeviceMessage.startMessage("playlist");
		controller.publish("/system", message);
		setText("starting application");
	}

	protected void uninstall() {
		String message = DeviceMessage.unInstallMessage("playlist");
		controller.publish("/system", message);
		setText("Uninstalling application");
	}

	protected void exist() {
		String message = DeviceMessage.existMessage("playlist");
		controller.publish("/system", message);
	}

	private void install() {
		StreamToBase64String streamToBase64String = StreamToBase64String.getInstance(this);
		String data = streamToBase64String.getBase64StringFromAssets("Playlist.zip");
		String message = DeviceMessage.installMessage(data);
		controller.publish("/system", message);
		setText("Installing from device");
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

		connect.setVisibility(View.GONE);
		install.setVisibility(View.GONE);
		exist.setVisibility(View.GONE);
		uninstall.setVisibility(View.GONE);
		start.setVisibility(View.GONE);
		stop.setVisibility(View.GONE);
		disconnect.setVisibility(View.GONE);
		installurl.setVisibility(View.GONE);

		previous = (ImageButton) findViewById(R.id.prev);
		next = (ImageButton) findViewById(R.id.next);
		play = (ImageButton) findViewById(R.id.play);
		pause = (ImageButton) findViewById(R.id.pause);

		previous.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				controller.previous();
			}
		});

		next.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				controller.next();
			}
		});

		play.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				controller.play();
			}
		});

		pause.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				controller.pause();
			}
		});

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

}