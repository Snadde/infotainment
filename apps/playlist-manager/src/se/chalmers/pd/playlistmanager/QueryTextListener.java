package se.chalmers.pd.playlistmanager;

import se.chalmers.pd.playlistmanager.AndroidSpotifyMetadata.Callback;
import android.widget.SearchView;

import com.mixtape.spotify.api.RequestType;


/**
 * This listener listens for changes in the text in the search box and also
 * for the submission of a query.
 */

public class QueryTextListener implements SearchView.OnQueryTextListener {
	
	private Callback callback;

	public QueryTextListener(AndroidSpotifyMetadata.Callback callback) {
		this.callback = callback;
	}
	
	@Override
	public boolean onQueryTextSubmit(String query) {
		search(query);
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// Don't care about text changes
		return true;
	}
	
	public void search(String query) {
		AndroidSpotifyMetadata s = new AndroidSpotifyMetadata();
		s.search(query, RequestType.track, callback);
	}
}
