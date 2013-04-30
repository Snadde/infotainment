package se.chalmers.pd.device;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ApplicationController controller;
	private TextView status;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		status = (TextView) findViewById(R.id.status);
		status.setMovementMethod(new ScrollingMovementMethod());
		controller = new ApplicationController(this);
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
				//installURL();
				startSong();
			}
		});
		
		
	}
	protected void startSong()
	{
		String uri = "spotify:track:1DlBOQRf6gGpAS0azPizo7";
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		startActivity(i);
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

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	public void setText(String text){
		status.setText(text);
	}
}