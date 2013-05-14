package se.chalmers.pd.playlistmanager;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

public class LoadingDialogFragment extends DialogFragment {

	public static LoadingDialogFragment newInstance() {
		LoadingDialogFragment fragment = new LoadingDialogFragment();
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		final ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage(getString(R.string.loading_message));
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		return dialog;
	}

}
