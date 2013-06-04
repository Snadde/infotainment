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

    /**
     * Instantiates the class with the callback that should be called when the search
     * begins and ends.
     *
     * @param callback the implementer of the Callback interface
     */
    public QueryTextListener(Callback callback) {
        this.callback = callback;
    }

    /**
     * Searches when the user submits the query.
     *
     * @param query the query to search for
     * @return always true
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        search(query);
        return true;
    }

    /**
     * Lets the callback know that the search has begun and then uses the android
     * wrapper for Spotify Metadata API to search.
     *
     * @param query the query to search for
     */
    public void search(String query) {
        callback.onSearchBegin();
        AndroidSpotifyMetadata spotify = new AndroidSpotifyMetadata();
        spotify.search(query, RequestType.track, this);
    }

    /**
     * When a result has been receive this calls back to the searcher with the result.
     *
     * @param tracks the result from the search
     */
    @Override
    public void onSearchResult(ArrayList<Track> tracks) {
        callback.onSearchResult(tracks);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Don't care about text changes
        return true;
    }
}
