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
    private static final String SPOTIFY = "spotify";
    private static final String SPOTIFY_WRAPPER = "spotifywrapper";
    private static final String USER_DETAILS = "userdetails.txt";
    private static final String PATH = "/Android/data/se.chalmers.pd.device";

	private boolean isPlaying = false;
    private boolean initiated = false;
	private ArrayList<Track> playlist;
	private PlaylistCallback playlistCallback;
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
		System.loadLibrary(SPOTIFY);
		System.loadLibrary(SPOTIFY_WRAPPER);
		LibSpotifyWrapper.init(LibSpotifyWrapper.class.getClassLoader(), Environment.getExternalStorageDirectory()
				.getAbsolutePath() + PATH);
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
	public void addTrackToPlaylist(String name, String artist, String spotifyUri, int length) {
		Track newTrack = new Track(name, artist, spotifyUri, length);
		playlist.add(newTrack);
	}

    /**
     * Adds a track to the playlist and if there is an empty list -> set
     * the track to current track
     * @param newTrack
     */
	public void addTrackToPlaylist(Track newTrack){
        playlist.add(newTrack);
	}
	
	/**
	 * Tries to start playing a track if it is not already playing or the playlist is empty.
	 * uses the callback to notify back to the application controller.
	 */
	public void play() {
		if (!isPlaying && !isEmptyPlaylist()) {
			LibSpotifyWrapper.togglePlay(getCurrentTrack().getUri());
			isPlaying = true;
            if(!initiated){
                initiated = true;
            }
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
			LibSpotifyWrapper.togglePlay(getCurrentTrack().getUri());
			isPlaying = false;
		} else {
			playlistCallback.onPause(false);
		}
	}
	
	/**
	 * Tries to play the next track of the playlist. If it is at the end 
	 * it starts over.
	 */
	public void playNext() {
		if (!isEmptyPlaylist()){
            Track originalTrack = playlist.get(0);
            playlist.remove(originalTrack);
            playlist.add(originalTrack);
		    LibSpotifyWrapper.playNext(getCurrentTrack().getUri());
	    }
    }
    /**
     * Tries to play the previous track of the playlist. If it is at the
     * beginning of hte palylist it starts at the end.
     */
    public void playPrevious() {
        if (!isEmptyPlaylist()){
            Track originalTrack = playlist.get(playlist.size() - 1);
            playlist.remove(originalTrack);
            playlist.add(0, originalTrack);
            LibSpotifyWrapper.playNext(originalTrack.getUri());
        }
    }

	/**
	 * Reads the user name and password for spotify in a file called userdetails.txt.
	 * Then tries to log in by calling the Lobsspotifywrapper.
	 */
	public void login() {
		try {
			InputStreamReader reader = new InputStreamReader(context.getAssets().open(USER_DETAILS));
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
	 * @return currentTrack
	 * 				the current track
	 */
	public Track getCurrentTrack() {
        return playlist.isEmpty() ? null : playlist.get(0);
    }
	/**
	 * Method for updating the tracks position
	 * @param position
	 */
	public void seek(float position) {
		if(initiated){
            LibSpotifyWrapper.seek(position);
        }
	}

    /**
     * Clears the playlist and resets the different
     * variables.
     */
	public void clearPlaylist() {
		seek(0);
        initiated = false;
        playlist.clear();
	}

    private boolean isEmptyPlaylist(){
        return playlist.isEmpty();
    }

}
