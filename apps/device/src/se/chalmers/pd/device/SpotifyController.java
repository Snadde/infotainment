package se.chalmers.pd.device;

import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

public class SpotifyController {

	interface PlaylistCallback{
		void onLoginSuccess();
		void onLoginFailed();
		void onPlay(boolean success);
		void onPause(boolean success);
		void onEndOfTrack();
		void onEndOfPlaylist();
	};
	
	private boolean isPlaying = false;
	private int currentTrackIndex = 0;
	private boolean emptyList = true;
	private ArrayList<Track> playlist;
	private PlaylistCallback playlistCallback;
	
	
	public SpotifyController(PlaylistCallback playlistCallback){
		this.playlistCallback = playlistCallback;
		init();
	}
	
	private void init(){
		playlist = new ArrayList<Track>();
		System.loadLibrary("spotify");
		System.loadLibrary("spotifywrapper");
		LibSpotifyWrapper.init(LibSpotifyWrapper.class.getClassLoader(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/se.chalmers.pd.device");
	}
	
	public void addTrackToPlaylist(String name, String artist, String spotifyUri){
		Track newTrack = new Track(name, artist, spotifyUri);
		playlist.add(newTrack);
		emptyList = false;
	}
	
	public void play(){
		if(!isPlaying && !emptyList){
			LibSpotifyWrapper.togglePlay(playlist.get(currentTrackIndex).getUri());
			isPlaying = true;
		}
		playlistCallback.onPlay(isPlaying);
	}
	
	public void pause(){
		if(isPlaying){
			LibSpotifyWrapper.togglePlay(playlist.get(currentTrackIndex).getUri());
			isPlaying = false;
		}
		playlistCallback.onPause(!isPlaying);
	}
	
	public void playNext(){
		currentTrackIndex++;
		if(playlist.size() <= currentTrackIndex){
			//callback to onEndOfPlaylist
			emptyList = true;
		}
		else{
			LibSpotifyWrapper.playNext(playlist.get(currentTrackIndex).getUri());
		}
	}
	
	public void login(){
		//TODO read username and password from from file 
		LibSpotifyWrapper.loginUser("", "");
	}
	
	public void destroy(){
		LibSpotifyWrapper.destroy();
	}
	
	public List<Track> getPlaylist(){
		return playlist;
	}
	
	public int getIndexOfCurrentTrack(){
		return currentTrackIndex;
	}
}
