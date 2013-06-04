package se.chalmers.pd.playlistmanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.os.AsyncTask;

import com.mixtape.spotify.api.Artist;
import com.mixtape.spotify.api.RequestType;
import com.mixtape.spotify.api.Response;
import com.mixtape.spotify.api.ResponseParser;

/**
 * This is an Android wrapper for the Spotify Metadata API library. It uses the library to search for tracks
 * from an AsyncTask. When a result has been received, it is converted into an ArrayList of Track objects
 * which is returned through the Callback interface and the method onSearchResult(ArrayList<Track> tracks).
 * <p/>
 * Note that the Track object returned from this class is different then the Track object in the library. This
 * class is also just working with tracks, not albums or artists.
 */
public class AndroidSpotifyMetadata {

    /**
     * Interface that the user of the class must implement.
     */
    public interface Callback {
        /**
         * Lets the listener know when then the search is finished and passes the result.
         *
         * @param a list of tracks
         */
        public void onSearchResult(ArrayList<Track> tracks);
    }

    private final static String QUERY_TEMPLATE = "http://ws.spotify.com/search/1/%s.json?q=%s&page=%d";
    private Callback callback;

    /**
     * Searches the API for a match. This method doesn't return anything, the
     * result is delivered through the onSearchResult callback.
     *
     * @param searchString the string to search for
     * @param type         the type, may be track, album, artist, note that this version of the class is written for tracks specifically.
     * @param callback     the callback which will receive the result
     */
    public void search(String searchString, RequestType type, Callback callback) {
        search(searchString, 1, type, callback);
    }

    /**
     * Searches the API for a match. This method doesn't return anything, the
     * result is delivered through the onSearchResult callback.
     *
     * @param searchString the string to search for
     * @param page         the page of the result set to return; defaults to 1
     * @param type         the type, may be track, album, artist, note that this version of the class is written for tracks specifically.
     * @param callback     the callback which will receive the result
     */
    public void search(String searchString, int page, RequestType type, Callback callback) {
        this.callback = callback;
        String urlEncodedSearchString;
        String searchUrl = "";
        try {
            urlEncodedSearchString = URLEncoder.encode(searchString, "UTF-8");
            searchUrl = String.format(QUERY_TEMPLATE, type.name(), urlEncodedSearchString, page);
            request(searchUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a request to the Spotify Metadata API through the SMAPI library in an asynchronous
     * request. It takes the response and converts it to an arraylist which is passed back
     * when the result is received. This only supports track search at the moment.
     *
     * @param searchUrl the url to the spotify api
     */
    private void request(String searchUrl) {
        final ArrayList<Track> tracks = new ArrayList<Track>();

        AsyncTask<String, Void, Response> worker = new AsyncTask<String, Void, Response>() {
            Response response;

            /**
             * Performs the request in the background.
             *
             * @param params url as first param
             * @return the response from the ResponseParser
             */
            @Override
            protected Response doInBackground(String... params) {
                URL url;
                try {
                    url = new URL(params[0]);
                    URLConnection connection = url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    response = ResponseParser.parse(inputStream);
                    inputStream.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }

            /**
             * Takes the response and converts it to an arraylist of custom Track objects
             *
             * @param response from the search
             */
            @Override
            protected void onPostExecute(Response response) {
                super.onPostExecute(response);
                if (response != null) {
                    if (response.getInfo().getType() == RequestType.track) {
                        for (com.mixtape.spotify.api.Track t : response.getTracks()) {
                            Artist a = (Artist) t.getArtists().toArray()[0];
                            Track track = new Track(t.getName(), a.getName(), t.getHref(), t.getLength());
                            tracks.add(track);
                        }
                    }
                }
                callback.onSearchResult(tracks);
            }
        };
        worker.execute(searchUrl);
    }

}
