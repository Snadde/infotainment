package se.chalmers.pd.playlistmanager;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

public class LoadingDialogFragment extends DialogFragment {

    private final String message;

    private LoadingDialogFragment(String message) {
        this.message = message;
    }

	public static LoadingDialogFragment newInstance(String message) {
		LoadingDialogFragment fragment = new LoadingDialogFragment(message);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage(message);
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		return dialog;
	}

}
