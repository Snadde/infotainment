package se.chalmers.pd.playlistmanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SearchFragment extends ListFragment {

	
	public SearchFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_search, null);
        return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);

		final List<Track> list = new ArrayList<Track>();
		list.add(new Track("Testname", "Test artist", "Test uri"));
		list.add(new Track("Testname2", "Test artist2", "Test uri"));
		list.add(new Track("Testname3", "Test artist3", "Test uri3"));

		ArrayAdapter<Track> adapter = new ArrayAdapter<Track>(getActivity(), android.R.layout.simple_list_item_2, list) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View row;
				Track track = list.get(position);
				if (convertView == null) {
					row = (View) inflater.inflate(android.R.layout.simple_list_item_2, null);
				} else {
					row = (View) convertView;
				}

				TextView v = (TextView) row.findViewById(android.R.id.text1);
				v.setText(track.getArtist());
				v.setTextColor(getResources().getColorStateList(android.R.color.holo_blue_dark));
				v = (TextView) row.findViewById(android.R.id.text2);
				StringBuilder sb = new StringBuilder();
				sb.append(track.getName());
				sb.append(", ");
				sb.append(track.getUri());
				v.setText(sb.toString());
				return row;
			}
		};
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

	}
}
