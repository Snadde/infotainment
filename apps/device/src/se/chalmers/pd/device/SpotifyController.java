package se.chalmers.pd.device;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;

public class SpotifyController {

	interface PlaylistCallback {
		void onLoginSuccess();

		void onLoginFailed(String message);

		void onPlay(boolean success);

		void onPause(boolean success);

		void onEndOfTrack();

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

	private void init() {
		playlist = new ArrayList<Track>();
		System.loadLibrary("spotify");
		System.loadLibrary("spotifywrapper");
		LibSpotifyWrapper.init(LibSpotifyWrapper.class.getClassLoader(), Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Android/data/se.chalmers.pd.device");
	}

	public void addTrackToPlaylist(String name, String artist, String spotifyUri) {
		Track newTrack = new Track(name, artist, spotifyUri);
		playlist.add(newTrack);
		emptyList = false;
	}

	public void play() {
		if (!isPlaying && !emptyList) {
			currentTrack = playlist.get(currentTrackIndex);
			LibSpotifyWrapper.togglePlay(currentTrack.getUri());
			isPlaying = true;
		} else {
			playlistCallback.onPlay(false);
		}
	}

	public void pause() {
		if (isPlaying) {
			LibSpotifyWrapper.togglePlay(currentTrack.getUri());
			isPlaying = false;
		} else {
			playlistCallback.onPause(false);
		}
	}

	public void playNext() {
		currentTrackIndex++;
		if (playlist.size() <= currentTrackIndex) {
			currentTrackIndex = 0;
		}

		currentTrack = playlist.get(currentTrackIndex);
		LibSpotifyWrapper.playNext(currentTrack.getUri());

	}

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

	public void destroy() {
		LibSpotifyWrapper.destroy();
	}

	public List<Track> getPlaylist() {
		return playlist;
	}

	public int getIndexOfCurrentTrack() {
		return currentTrackIndex;
	}

	public Track getCurrentTrack() {
		return currentTrack;
	}
}
