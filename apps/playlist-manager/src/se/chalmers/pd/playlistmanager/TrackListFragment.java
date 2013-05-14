package se.chalmers.pd.playlistmanager;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TrackListFragment extends ListFragment {

	
	public TrackListFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArrayList<Track> tracks = getArguments().getParcelableArrayList("tracks");
		setupAdapter(tracks);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_search, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

	}

	public void updateResults(ArrayList<Track> tracks) {
		setupAdapter(tracks);
	}
	
	private void setupAdapter(final ArrayList<Track> tracks) {
		ArrayAdapter<Track> adapter = new TrackAdapter(getActivity(), android.R.layout.simple_list_item_2, tracks);
		setListAdapter(adapter);
	}

}
