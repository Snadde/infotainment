package se.chalmers.pd.device;

import se.chalmers.pd.device.SpotifyController.PlaylistCallback;
import android.os.Handler;

public class LibSpotifyWrapper {

	private static Handler handler = new Handler();
	private static PlaylistCallback callback;

	native public static void init(ClassLoader loader, String storagePath);

	native public static void destroy();

	native private static void login(String username, String password);

	native private static void toggleplay(String uri);

	native private static void playnext(String uri);

	native public static void seek(float position);

	native public static void star();

	native public static void unstar();

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

	public static void onLogin(final boolean success, final String message) {
		handler.post(new Runnable() {
			public void run() {
				if (success) {
					callback.onLoginSuccess();
					System.out.println("onLogin success");
				} else {
					System.out.println("onLogin fail");
					callback.onLoginFailed(message);

				}
			}
		});

	}

	public static void onPlayerEndOfTrack() {
		handler.post(new Runnable() {

			public void run() {
				callback.onEndOfTrack();
			}
		});
	}

	public static void onPlayerPositionChanged(final float position) {
		handler.post(new Runnable() {

			public void run() {
				callback.onPositionChanged(position);

			}
		});
	}

	public static void onPlayerPause() {
		handler.post(new Runnable() {

			public void run() {
				callback.onPause(true);
			}
		});
	}

	public static void onPlayerPlay() {
		handler.post(new Runnable() {

			public void run() {
				callback.onPlay(true);
				System.out.println("onPlayerPlay");
			}
		});
	}

	static private float simTimer;

	static void simulateTimer() {
		handler.postDelayed(new Runnable() {

			public void run() {

				// mPlayerPositionDelegate.onPlayerPositionChanged(simTimer);
				simTimer += 0.1;
				simulateTimer();

			}
		}, 1000);
	}
	public static void onTrackStarred() {
		handler.post(new Runnable() {

			public void run() {
			//	mPlayerPositionDelegate.onTrackStarred();

			}
		});
	}

	public static void onTrackUnStarred() {
		handler.post(new Runnable() {

			public void run() {
			//	mPlayerPositionDelegate.onTrackUnStarred();

			}
		});
	}

}