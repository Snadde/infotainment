package se.chalmers.pd.device;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private boolean isBound;
	private ApplicationController controller;
	private MQTTService mqttService;
	private TextView status;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		status = (TextView) findViewById(R.id.status);
		status.setMovementMethod(new ScrollingMovementMethod());
		setupButtons();
	}
	
	private void setupButtons(){
		Button connect, install, exist, uninstall, start, stop, disconnect, installurl;
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
				bindService();				
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
		String message = DeviceMessage.installFromUrlMessage("webapp");
		controller.publish("/system", message);
		setText("Installing from URL");
	}

	protected void disconnect() {
		controller.disconnect();
		setText("Disconnecting");
	}

	protected void stop() {
		String message = DeviceMessage.stopMessage("webapp");
		controller.publish("/system", message);
		setText("Stopping application");
	}

	protected void start() {
		String message = DeviceMessage.startMessage("webapp");
		controller.publish("/system", message);
		setText("starting application");
	}

	protected void uninstall() {
		String message = DeviceMessage.unInstallMessage("webapp");
		controller.publish("/system", message);	
		setText("Uninstalling application");
	}

	protected void exist() {
		String message = DeviceMessage.existMessage("webapp");
		controller.publish("/system", message);	
	}

	private void install(){
		StreamToBase64String streamToBase64String = StreamToBase64String.getInstance(this);
		String data = streamToBase64String.getBase64StringFromAssets("webapp.zip");
		String message = DeviceMessage.installMessage(data);
		controller.publish("/system", message);	
		setText("Installing from device");
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * A local implementation of the service connection callbacks that are
	 * needed to that we can keep track of then the service has been launched,
	 * stopped or disconnected.
	 */
	private ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mqttService = ((MQTTService.LocalBinder) service).getService();
			controller = new ApplicationController(mqttService, MainActivity.this);
		}

		public void onServiceDisconnected(ComponentName className) {
			mqttService = null;
		}

		// TODO Add reconnect feature
	};

	/**
	 * Binds this activity to the mqtt service so we can communicate between
	 * them
	 */
	private void bindService() {
		Intent intent = new Intent(MainActivity.this, MQTTService.class);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
		startService(intent);
		isBound = true;
	}

	/**
	 * Unbinds the service when it's no longer needed
	 */
	private void unbindService() {
		if (isBound) {
			unbindService(serviceConnection);
			isBound = false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService();
	}
	
	public void setText(String text){
		status.append("\n" + text);
	}
}