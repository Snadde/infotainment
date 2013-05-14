package se.chalmers.pd.playlistmanager;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TrackAdapter extends ArrayAdapter<Track> {

	private List<Track> tracks;
	private Context context;

	public TrackAdapter(Context context, int textViewResourceId, List<Track> tracks) {
		super(context, textViewResourceId, tracks);
		this.tracks = tracks;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			row = (View) inflater.inflate(android.R.layout.simple_list_item_2, null);
		} else {
			row = (View) convertView;
		}
		
		final Track track = tracks.get(position);
		TextView v = (TextView) row.findViewById(android.R.id.text1);
		v.setText(track.getName());
		v.setTextColor(context.getResources().getColorStateList(android.R.color.holo_blue_dark));
		v = (TextView) row.findViewById(android.R.id.text2);
		StringBuilder sb = new StringBuilder();
		sb.append(track.getArtist());
		sb.append(", ");
		sb.append(track.getUri());
		v.setText(sb.toString());
		return row;
	}

}
