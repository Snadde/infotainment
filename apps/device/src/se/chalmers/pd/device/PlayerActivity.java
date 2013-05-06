package se.chalmers.pd.device;


import se.chalmers.pd.device.SpotifyController.PlaylistCallback;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;


public class PlayerActivity extends Activity implements PlaylistCallback{

	private TextView textview;
	private ImageButton previous;
	private ImageButton play;
	private ImageButton next;
	private SpotifyController spotifyController;
	

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_player);
	textview = (TextView) findViewById(R.id.current_playing);
	spotifyController = new SpotifyController(this, this);
	setupPlaylist();
	spotifyController.login();
	setupButtons();
}


private void setupButtons() {
	previous = (ImageButton) findViewById(R.id.prev);
	next = (ImageButton) findViewById(R.id.next);
	play = (ImageButton) findViewById(R.id.play);
	
	previous.setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
			spotifyController.pause();
		}
	});
	
	next.setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
			spotifyController.playNext();
		}
	});
	
	play.setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
			spotifyController.play();
		}
	});
}

private void setupPlaylist() {
	
	spotifyController.addTrackToPlaylist("The pretender", "Foo Fighters", "spotify:track:3ZsjgLDSvusBgxGWrTAVto");
	//send to mqtt when initiated
	spotifyController.addTrackToPlaylist("Rape me", "Nirvana", "spotify:track:47KVHb6cOVBZbmXQweE5p7");
	spotifyController.addTrackToPlaylist("X you", "Avicii", "spotify:track:330r0K82tIDVr6f1GezAd8");
	
}

public void onLoginSuccess() {
	// TODO Auto-generated method stub
	
}


public void onLoginFailed() {
	// TODO Auto-generated method stub
	
}


public void onPlay(boolean success) {
	if(success){
		int index = spotifyController.getIndexOfCurrentTrack();
		Track currentTrack = spotifyController.getPlaylist().get(index);
		textview.setText("Currently Playing : " + currentTrack.getArtist() + " - " + currentTrack.getName());
	}
	
}


public void onPause(boolean success) {
	// TODO Auto-generated method stub
	
}


public void onEndOfTrack() {
	// TODO Auto-generated method stub
	
}


public void onEndOfPlaylist() {
	// TODO Auto-generated method stub
	
}

@Override
protected void onDestroy() {
	spotifyController.destroy();
	super.onDestroy();
}


}
