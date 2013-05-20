package se.chalmers.pd.playlistmanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogFactory {
	
	public interface Callback {
		public void onConnectDialogAnswer(boolean result);
	}

	public static AlertDialog buildConnectDialog(Context context, final Callback callback) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(context.getString(R.string.connect_error_title));
		alertDialogBuilder.setMessage(context.getString(R.string.connect_dialog_message)).setCancelable(false)
				.setPositiveButton(context.getString(R.string.reconnect), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						callback.onConnectDialogAnswer(true);
					}
				}).setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callback.onConnectDialogAnswer(false);
                dialog.cancel();
            }
        });
		return alertDialogBuilder.create();
	}
	
	public static ProgressDialog buildLoadingDialog() {
		return (ProgressDialog) LoadingDialogFragment.newInstance().getDialog();
	}
}
