package se.chalmers.pd.device;

import se.chalmers.pd.device.SpotifyController.PlaylistCallback;
import android.os.Handler;

public class LibSpotifyWrapper {

	private static Handler handler = new Handler();
	private static PlaylistCallback callback;

    /**
     *  The Native methods accessable from the java code.
     */
	native public static void init(ClassLoader loader, String storagePath);

	native public static void destroy();

	native private static void login(String username, String password);

	native private static void toggleplay(String uri);

	native private static void playnext(String uri);

	native public static void seek(float position);

	public static void loginUser(String username, String password, PlaylistCallback playlistCallback) {
		callback = playlistCallback;
		login(username, password);
	}

	public static void togglePlay(String uri) {
		toggleplay(uri);
	}

	public static void playNext(String uri) {
		playnext(uri);
	}

    /**
     * When a login attempt is either successful or fail notify Playlistcallback
     * @param success
     * @param message
     */
	public static void onLogin(final boolean success, final String message) {
		handler.post(new Runnable() {
			public void run() {
				if (success) {
					callback.onLoginSuccess();
				} else {
					callback.onLoginFailed(message);

				}
			}
		});

	}

    /**
     * When a track has ended notify the callback to PlaylistCallback
     */
	public static void onPlayerEndOfTrack() {
		handler.post(new Runnable() {

			public void run() {
				callback.onEndOfTrack();
			}
		});
	}

    /**
     * When a player position has changed notify the callback to PlaylistCallback
     * @param position
     */
	public static void onPlayerPositionChanged(final float position) {
		handler.post(new Runnable() {

			public void run() {
				callback.onPositionChanged(position);

			}
		});
	}

    /**
     * When a track has paused notify the callback PlaylistCallback
     */
	public static void onPlayerPause() {
		handler.post(new Runnable() {

			public void run() {
				callback.onPause(true);
			}
		});
	}

    /**
     * When a track has started playing notify PlaylistCallback
     */
	public static void onPlayerPlay() {
		handler.post(new Runnable() {

			public void run() {
				callback.onPlay(true);
			}
		});
	}
}