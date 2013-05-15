package se.chalmers.pd.playlistmanager;

import android.widget.SearchView;

import com.mixtape.spotify.api.RequestType;


/**
 * This listener listens for changes in the text in the search box and also
 * for the submission of a query.
 */

public class QueryTextListener implements SearchView.OnQueryTextListener {
	
	public interface Callback {
		public void onSearchBegin();
	}
	
	private AndroidSpotifyMetadata.Callback spotifyCallback;
	private Callback searchCallback;

	public QueryTextListener(AndroidSpotifyMetadata.Callback spotifyCallback, Callback searchCallback) {
		this.spotifyCallback = spotifyCallback;
		this.searchCallback = searchCallback;
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
		searchCallback.onSearchBegin();
		AndroidSpotifyMetadata s = new AndroidSpotifyMetadata();
		s.search(query, RequestType.track, spotifyCallback);
	}
}
