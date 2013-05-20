package se.chalmers.pd.playlistmanager;

import java.util.ArrayList;

import android.widget.SearchView;

import com.mixtape.spotify.api.RequestType;


/**
 * This listener listens for changes in the text in the search box and also
 * for the submission of a query.
 */

public class QueryTextListener implements SearchView.OnQueryTextListener, AndroidSpotifyMetadata.Callback {
	
	public interface Callback {
		public void onSearchBegin();
		public void onSearchResult(ArrayList<Track> tracks);
	}
	
	private Callback callback;

	public QueryTextListener(Callback callback) {
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
		callback.onSearchBegin();
		AndroidSpotifyMetadata spotify = new AndroidSpotifyMetadata();
		spotify.search(query, RequestType.track, this);
	}

	@Override
	public void onSearchResult(ArrayList<Track> tracks) {
		callback.onSearchResult(tracks);		
	}
}
