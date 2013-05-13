package se.chalmers.pd.device;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;

/**
 * This class has the playlist and the connection with the libsspotifywrapper.
 * It initializes the wrapper class and forwards different actions.
 * 
 * @author Patrik Thituson
 * 
 */
public class SpotifyController {

	interface PlaylistCallback {
		void onLoginSuccess();

		void onLoginFailed(String message);

		void onPlay(boolean success);

		void onPause(boolean success);

		void onEndOfTrack();
		
		void onPositionChanged(float position);

	}

	private static final int USER_NAME = 0;
	private static final int PASSWORD = 1;

	private boolean isPlaying = false;
	private int currentTrackIndex = 0;
	private boolean emptyList = true;
	private ArrayList<Track> playlist;
	private PlaylistCallback playlistCallback;
	private Track currentTrack;
	private Context context;

	public SpotifyController(PlaylistCallback playlistCallback, Context context) {
		this.playlistCallback = playlistCallback;
		this.context = context;
		init();
	}

	/**
	 * Initiates the spotifywrapper by loading the libraries and playlist
	 */
	private void init() {
		playlist = new ArrayList<Track>();
		System.loadLibrary("spotify");
		System.loadLibrary("spotifywrapper");
		LibSpotifyWrapper.init(LibSpotifyWrapper.class.getClassLoader(), Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Android/data/se.chalmers.pd.device");
	}

	/**
	 * Adds a track to the playlist.
	 * 
	 * @param name
	 *            the name of the track
	 * @param artist
	 *            name of the artist
	 * @param spotifyUri
	 *            the uri of the track
	 */
	public void addTrackToPlaylist(String name, String artist, String spotifyUri) {
		Track newTrack = new Track(name, artist, spotifyUri);
		playlist.add(newTrack);
		emptyList = false;
	}
	
	/**
	 * Tries to start playing a track if it is not already playing or the playlist is empty.
	 * uses the callback to notify back to the application controller.
	 */
	public void play() {
		if (!isPlaying && !emptyList) {
			currentTrack = playlist.get(currentTrackIndex);
			LibSpotifyWrapper.togglePlay(currentTrack.getUri());
			isPlaying = true;
		} else {
			playlistCallback.onPlay(false);
		}
	}
	
	/**
	 * Tries to pause the playing a track if it is not already paused.
	 * uses the callback to notify back to the application controller.
	 */
	public void pause() {
		if (isPlaying) {
			LibSpotifyWrapper.togglePlay(currentTrack.getUri());
			isPlaying = false;
		} else {
			playlistCallback.onPause(false);
		}
	}
	
	/**
	 * Tries to play the next track of the playlist. If it is at the end 
	 * it starts over.
	 * uses the callback to notify back to the application controller.
	 */
	public void playNext() {
		currentTrackIndex++;
		if (playlist.size() <= currentTrackIndex) {
			currentTrackIndex = 0;
		}

		currentTrack = playlist.get(currentTrackIndex);
		LibSpotifyWrapper.playNext(currentTrack.getUri());

	}
	/**
	 * Reads the user name and password for spotify in a file called userdetails.txt.
	 * Then tries to log in by calling the Lobsspotifywrapper.
	 */
	public void login() {
		try {
			InputStreamReader reader = new InputStreamReader(context.getAssets().open("userdetails.txt"));
			char[] buf = new char[100];
			int length = reader.read(buf);
			String userDetails = new String(buf, 0, length);
			String[] loginDetails = userDetails.split(",");
			LibSpotifyWrapper.loginUser(loginDetails[USER_NAME], loginDetails[PASSWORD], this.playlistCallback);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Destroys the libspotifywrapper
	 */
	public void destroy() {
		LibSpotifyWrapper.destroy();
	}
	/**
	 * 
	 * @return playlist
	 * 				the playlist with all tracks
	 */
	public List<Track> getPlaylist() {
		return playlist;
	}
	/**
	 * 
	 * @return currentTrackIndex
	 * 				the index of the current track
	 */
	public int getIndexOfCurrentTrack() {
		return currentTrackIndex;
	}

	/**
	 * 
	 * @return currentTrack
	 * 				the current track
	 */
	public Track getCurrentTrack() {
		return currentTrack;
	}
	/**
	 * Method for updating the tracks position
	 * @param position
	 */
	public void seek(float position) {
		LibSpotifyWrapper.seek(position);
	}

}
