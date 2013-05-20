package se.chalmers.pd.device;

import se.chalmers.pd.device.ApplicationController.Callbacks;
import se.chalmers.pd.device.NfcReader.NFCCallback;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * This Class is separated from all logic and consist only of the view part of
 * the application. It sets up all the buttons and adds onclicklisteners.
 * 
 * @author Patrik Thituson
 * 
 */
public class MainActivity extends Activity implements Callbacks, View.OnClickListener, NFCCallback {

	private ApplicationController controller;
	private TextView currentTrack, status;
	private ImageButton previous, play, next, pause;
	private ToggleButton start;
	private MenuItem connect, disconnect, install, uninstall;
	private SeekBar seekbar;
	private NfcReader nfcReader;
	private LoadingDialogFragment loadingDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		currentTrack = (TextView) findViewById(R.id.currenttrack);
		status = (TextView) findViewById(R.id.status);
		seekbar = (SeekBar) findViewById(R.id.seekbar);
		controller = new ApplicationController(this);
		setupButtons();
		// log in to spotify
		controller.login();
		nfcReader = new NfcReader(this);
	}

	/**
	 * Sets up all the buttons for the view and adds onclick listeners.
	 */
	private void setupButtons() {

		start = (ToggleButton) findViewById(R.id.start);
		previous = (ImageButton) findViewById(R.id.prev);
		next = (ImageButton) findViewById(R.id.next);
		play = (ImageButton) findViewById(R.id.play);
		pause = (ImageButton) findViewById(R.id.pause);
		start.setOnClickListener(this);
		previous.setOnClickListener(this);
		next.setOnClickListener(this);
		play.setOnClickListener(this);
		pause.setOnClickListener(this);
		seekbar.setMax(300);
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {
				controller.seek((float) seekBar.getProgress() / seekBar.getMax());
			}

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);

		connect = (MenuItem) menu.findItem(R.id.connect);
		install = (MenuItem) menu.findItem(R.id.install);
		uninstall = (MenuItem) menu.findItem(R.id.uninstall);
		disconnect = (MenuItem) menu.findItem(R.id.disconnect);
		install.setEnabled(false);
		disconnect.setEnabled(false);
		uninstall.setEnabled(false);
		return true;
	}

	/**
	 * Calls the controllers onDestroy to prevent crash in other threads
	 */
	@Override
	protected void onDestroy() {
		controller.onDestroy();
		super.onDestroy();

	}

	/**
	 * Sets the text of stats view
	 * 
	 * @param text
	 */
	public void setText(String text) {
		currentTrack.setText(text);
	}

	/**
	 * Helper method that makes the Player buttons visible called when user is
	 * loged in.
	 */
	private void showPlayerButtons() {

		previous.setVisibility(View.VISIBLE);
		play.setVisibility(View.VISIBLE);
		next.setVisibility(View.VISIBLE);
	}

	/**
	 * Callback that is called when the user has successfully logged in
	 */
	public void onPlayerLoggedIn() {
		showPlayerButtons();
	}

	/**
	 * Callback that is called when the Player has started playing
	 */
	public void onPlayerPlay() {
		runOnUiThread(new Runnable() {
			public void run() {
				play.setVisibility(View.GONE);
				pause.setVisibility(View.VISIBLE);
				setCurrentTrack();
			}
		});
	}

	/**
	 * Callback that is called when the Player has paused playing
	 */
	public void onPlayerPause() {
		runOnUiThread(new Runnable() {
			public void run() {
				play.setVisibility(View.VISIBLE);
				pause.setVisibility(View.GONE);
			}
		});
	}

	/**
	 * Callback that is called when the Player has paused playing
	 */
	public void onPlayerNext() {
		runOnUiThread(new Runnable() {
			public void run() {
				setCurrentTrack();
			}
		});
	}

	/**
	 * Callback that is called when the Web application has started successfully
	 * or stopped. The parameter 'show' will decide whether to show or hide the
	 * buttons
	 */
	public void onStartedApplication(final String status) {
		runOnUiThread(new Runnable() {
			public void run() {
				loadingDialog.dismiss();
				String message = "";
				if (status.equals("start")) {
					uninstall.setEnabled(false);
					start.setChecked(true);
					message = "Started application";
				} else if (status.equals("stop")) {
					uninstall.setEnabled(true);
					start.setChecked(false);
					message = "Stopped application";
				} else if (status.equals("error")) {
					start.setChecked(false);
					alert("No application installed !");
				}
				changeStatus(message);
			}
		});

	}
	public void onPendingAction(String message){
		loadingDialog = LoadingDialogFragment.newInstance(message);
		loadingDialog.show(getFragmentManager(), "loadingDialog");
	}

	/**
	 * Callback that is called when we want to change the visibility of the
	 * buttons start, uninstall and install.
	 */
	public void onInstalledApplication(final boolean show) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (show) {
					uninstall.setEnabled(true);
					changeStatus("Installed application found");
				} else {
					install.setEnabled(true);
					changeStatus("No installed application");
				}
			}
		});
	}

	/**
	 * Callback that is called when the application is connected with the broker
	 */
	public void onConnectedMQTT(final boolean connected) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (connected) {
					connect.setEnabled(false);
					disconnect.setEnabled(true);
					changeStatus("Connected to broker");
				} else {
					connect.setEnabled(true);
					disconnect.setEnabled(false);
					install.setEnabled(false);
					uninstall.setEnabled(false);
					changeStatus("Disconnected from broker");
				}

			}

		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.install:
			controller.install();
			return true;
		case R.id.uninstall:
			controller.uninstall();
			return true;
		case R.id.connect:
			controller.connect("tcp://192.168.43.147:1883"); // Backup button for connection with broker if NFC is unavailable
			return true;
		case R.id.disconnect:
			controller.disconnect();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * The onclick method will check which button was clicked and forward this
	 * to the controller.
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start:
			if (controller.isConnectedToBroker()) {
				if (start.isChecked()) {
					start.setChecked(false);
					controller.start();
				} else {
					controller.stop();
				}
			} else {
				start.setChecked(false);
				alert("Not connected to broker! ");
			}
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
		setCurrentTrack();
	}

	private void setCurrentTrack() {
		setText(controller.getCurrentTrack());
	}

	private void alert(String message) {
		AlertDialog alert = new AlertDialog.Builder(this).setTitle("Warning").setMessage(message)
				.setNeutralButton("ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// TODO maybe implement ? controller.connect();
						dialog.cancel();
					}
				}).create();
		alert.show();
	}

	private void changeStatus(String message) {
		status.setText(message);
	}

	public void onUpdateSeekbar(float position) {
		seekbar.setProgress((int) (position * seekbar.getMax()));
	}

	public void onNFCResult(String url) {
		controller.connect(url);		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		nfcReader.onPause();
	}		
	@Override	
	protected void onResume() {
		super.onResume();
		nfcReader.onResume();
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		nfcReader.onNewIntent(intent);
	}
}