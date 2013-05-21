package se.chalmers.pd.playlistmanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PlayerFragment extends Fragment implements View.OnClickListener {

	private View pause;
	private View play;
	private TextView trackInfo;
	private Track currentTrack;

	public PlayerFragment() {
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle arguments = getArguments();
		currentTrack = (Track) arguments.getParcelable("track");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_player, null);
		setupButtons(rootView);
		updateTrack(currentTrack);
		return rootView;
		
	}

	/**
	 * Sets up all the buttons for the view and adds onclick listeners.
	 */
	private void setupButtons(View rootView) {
		View previous = rootView.findViewById(R.id.prev);
		View next = rootView.findViewById(R.id.next);
		
		play = rootView.findViewById(R.id.play);
		pause = rootView.findViewById(R.id.pause);
		previous.setOnClickListener(this);
		next.setOnClickListener(this);
		play.setOnClickListener(this);
		pause.setOnClickListener(this);
		
		trackInfo = (TextView) rootView.findViewById(R.id.track_info);
		
	}

	@Override
	public void onClick(View v) {
		Action action = Action.NONE;
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

	public void updateAction(Action action) {
		switch(action) {
		case pause:
		case play:
			togglePlayPause();
			break;
		}
	}

	private void togglePlayPause() {
		if (play.getVisibility() == View.VISIBLE) {
			pause.setVisibility(View.VISIBLE);
			play.setVisibility(View.GONE);
		} else {
			pause.setVisibility(View.GONE);
			play.setVisibility(View.VISIBLE);
		}
	}

	public void updateTrack(Track track) {
		currentTrack = track;
		trackInfo.setText(currentTrack.getArtist() + " - " + currentTrack.getName());
	}
}
