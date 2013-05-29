package se.chalmers.pd.playlistmanager;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

/**
 * This dialog fragment shows a simple dialog to the user with a spinner and a message supplied by
 * the instantiator.
 */
public class LoadingDialogFragment extends DialogFragment {

    private final String message;

    /**
     * Private constructor which takes the message string as parameter
     *
     * @param message the message to display
     */
    private LoadingDialogFragment(String message) {
        this.message = message;
    }

    /**
     * Static method that should be used to create an instance of the dialog.
     *
     * @param message the message to display
     * @return a new loading dialog fragment
     */
    public static LoadingDialogFragment newInstance(String message) {
        LoadingDialogFragment fragment = new LoadingDialogFragment(message);
        return fragment;
    }

    /**
     * Called by system when the dialog is created. Sets the message and some dialog properties.
     *
     * @param savedInstanceState
     * @return the dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        return dialog;
    }

}
