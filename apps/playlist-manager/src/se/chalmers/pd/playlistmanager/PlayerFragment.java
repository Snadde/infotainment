package se.chalmers.pd.playlistmanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlayerFragment extends Fragment implements View.OnClickListener {
	
	public PlayerFragment() {};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_player, null);
		setupButtons(rootView);
		return rootView;
	}
	
	/**
	 * Sets up all the buttons for the view and adds onclick listeners.
	 */
	private void setupButtons(View rootView) {
		View previous = rootView.findViewById(R.id.prev);
		View next = rootView.findViewById(R.id.next);
		View play = rootView.findViewById(R.id.play);
		View pause = rootView.findViewById(R.id.pause);
		previous.setOnClickListener(this);
		next.setOnClickListener(this);
		play.setOnClickListener(this);
		pause.setOnClickListener(this);		
	}

	@Override
	public void onClick(View v) {
		
		Action action;
		switch (v.getId()) {
		case R.id.play:
			action = Action.play;
			break;
		case R.id.next:
			action = Action.next;
			break;
		case R.id.pause:
			action = Action.pause;
			break;
		case R.id.prev:
			action = Action.prev;
			break;
		}
		((MainActivity) getActivity()).onPlayerAction(action);
	}
}
