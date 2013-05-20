package se.chalmers.pd.playlistmanager;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class TrackListFragment extends ListFragment {
	
	private String title;
	private TrackAdapter adapter;

	public TrackListFragment() { }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		title = getArguments().getString("title", "");
		ArrayList<Track> tracks = new ArrayList<Track>();
		setupAdapter(tracks);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tracklist, null);
		TextView titleView = (TextView) view.findViewById(R.id.title);
		titleView.setText(title);
		return view;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Track track = (Track) getListAdapter().getItem(position);
		((MainActivity)getActivity()).onTrackSelected(track);
	}
	
	public void updateResults(ArrayList<Track> tracks) {
		setupAdapter(tracks);
	}
	
	private void setupAdapter(final ArrayList<Track> tracks) {
		adapter = new TrackAdapter(getActivity(), android.R.layout.simple_list_item_2, tracks);
		setListAdapter(adapter);
	}
	

	public void resetPlaylist() {
		adapter.clear();
	}

	public void addToPlaylist(Track track) {
		adapter.add(track);
		if(adapter.getCount() == 1) {
			updatePlayer();
		}
	}

	public void updateAction(Action action) {
		switch(action) {
		case next:
			highlightNext();
			updatePlayer();
			break;
		case prev:
			highlightPrev();
			updatePlayer();
			break;
		}
	}
	
	private void highlightNext() {
		Track originalTrack = adapter.getItem(0);
		adapter.remove(originalTrack);
		adapter.add(originalTrack);
		adapter.notifyDataSetChanged();
	}

	private void highlightPrev() {
		Track originalTrack = adapter.getItem(adapter.getCount() - 1);
		adapter.remove(originalTrack);
		adapter.insert(originalTrack, 0);
		adapter.notifyDataSetChanged();
	}

	private void updatePlayer() {
		((MainActivity)getActivity()).updatePlayer(adapter.getItem(0));
	}
}
