package se.chalmers.pd.device;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public class DialogFactory {

    public interface Callback {
        public void onConnectDialogAnswer(boolean result, String newBrokerUrl);
    }

    /**
     * Creates a Dialog with a specified message and allows the user to connect
     * to the broker by inputting the URL. Uses callback to notify the main activity.
     * @param context
     * @param callback
     * @param url the url to show as pre filled
     * @param messageStringId the message to show
     * @return
     */
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

}