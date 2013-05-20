package se.chalmers.pd.device;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

public class LoadingDialogFragment extends DialogFragment {

	private static String dialogmessage;

	public static LoadingDialogFragment newInstance(String message) {
		dialogmessage = message;
		LoadingDialogFragment fragment = new LoadingDialogFragment();
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		final ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage(dialogmessage);
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		return dialog;
	}

}