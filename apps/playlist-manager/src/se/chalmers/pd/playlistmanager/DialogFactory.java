package se.chalmers.pd.playlistmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public class DialogFactory {
	
	public interface Callback {
		public void onConnectDialogAnswer(boolean result, String newBrokerUrl);
	}

	public static AlertDialog buildConnectToUrlDialog(Context context, final Callback callback, String url, int messageStringId) {
        final EditText input = new EditText(context);
        input.setHint(context.getString(R.string.broker_url_pattern));
        input.setText(url);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
            .setView(input)
            .setTitle(context.getString(R.string.connect_title))
		    .setMessage(context.getString(messageStringId))
            .setCancelable(false)
			.setPositiveButton(context.getString(R.string.connect), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    callback.onConnectDialogAnswer(true, input.getText().toString());
                }
            })
            .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    callback.onConnectDialogAnswer(false, input.getText().toString());
                    dialog.cancel();
                }
            });
		return alertDialogBuilder.create();
	}
	
	public static LoadingDialogFragment buildLoadingDialog(Context context) {
		return LoadingDialogFragment.newInstance(context.getString(R.string.loading_message));
	}

    public static LoadingDialogFragment buildConnectingDialog(Context context, String url) {
        return LoadingDialogFragment.newInstance(context.getString(R.string.connecting_to) + " " + url);
    }
}
