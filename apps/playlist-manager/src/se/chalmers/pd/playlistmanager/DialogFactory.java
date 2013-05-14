package se.chalmers.pd.playlistmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogFactory {
	
	public interface Callback {
		public void onConnectDialogAnswer(boolean result);
	}

	public static AlertDialog buildConnectDialog(Context context, final Callback callback) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Connection error");
		alertDialogBuilder.setMessage("There was a problem connecting to the broker, what do you want to do?").setCancelable(false)
				.setPositiveButton("Reconnect", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						callback.onConnectDialogAnswer(true);
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						callback.onConnectDialogAnswer(false);
						dialog.cancel();
					}
				});
		return alertDialogBuilder.create();
	}
}
