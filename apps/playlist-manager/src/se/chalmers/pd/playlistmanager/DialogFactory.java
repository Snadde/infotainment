package se.chalmers.pd.playlistmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

/**
 * This class allows for easy creation of the dialogs that can be shown in the application. It can build
 * a connect dialog, loading dialog and a connecting dialog. The methods are all static and should
 * be accessed in a static way.
 * <p/>
 * The class uses a callback to the instantiator to let it know when the user has selected an option.
 */
public class DialogFactory {

    /**
     * Callback for when the user answers a dialog.
     */
    public interface Callback {
        /**
         * Callback for when the user answers a dialog.
         *
         * @param result       true if positive answer, false otherwise
         * @param newBrokerUrl new broker url to connect to
         */
        public void onConnectDialogAnswer(boolean result, String newBrokerUrl);
    }

    /**
     * Builds a dialog containing an edit text view which can be edited with a new broker url. The url
     * is by default the last known to the application.
     *
     * @param context         the context to show the dialog in
     * @param callback        the callback to call when an answered is received
     * @param url             the old url
     * @param messageStringId the resource id of the string to show as message.
     * @return an alert dialog with the edit text view for a new url
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

    /**
     * Builds a loading dialog with a loading message.
     *
     * @param context to show the dialog in
     * @return a loading dialog
     */
    public static LoadingDialogFragment buildLoadingDialog(Context context) {
        return LoadingDialogFragment.newInstance(context.getString(R.string.loading_message));
    }

    /**
     * Builds a connecting dialog which contains information about the url the application
     * is connecting to and shows it to the user.
     *
     * @param context the context to show the dialog in
     * @param url     the url the application is connecting to
     * @return a dialog with connecting message
     */
    public static LoadingDialogFragment buildConnectingDialog(Context context, String url) {
        return LoadingDialogFragment.newInstance(context.getString(R.string.connecting_to) + " " + url);
    }
}
