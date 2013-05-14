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

public class AndroidSpotifyMetadata {

	public interface Callback {
		public void onSearchResult(ArrayList<Track> tracks);
	}
	
	private final static String QUERY_TEMPLATE = "http://ws.spotify.com/search/1/%s.json?q=%s&page=%d";
	private Callback callback;

	public void search(String searchString, RequestType type, Callback callback) {
		search(searchString, 1, type, callback);
	}

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

	private void request(String searchUrl) {
		final ArrayList<Track> tracks = new ArrayList<Track>();
		
		AsyncTask<String, Void, Response> worker = new AsyncTask<String, Void, Response>() {
			Response response;
			
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

			@Override
			protected void onPostExecute(Response response) {
				super.onPostExecute(response);
				if (response.getInfo().getType() == RequestType.track) {
					for (com.mixtape.spotify.api.Track t : response.getTracks()) {
						Artist a = (Artist) t.getArtists().toArray()[0];
						Track track = new Track(t.getName(), a.getName(), t.getHref(), t.getLength());
						tracks.add(track);
					}
				}
				callback.onSearchResult(tracks);
			}
		};
		worker.execute(searchUrl);
	}

}
