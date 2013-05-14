package se.chalmers.pd.playlistmanager;

import java.util.ArrayList;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements AndroidSpotifyMetadata.Callback, ApplicationController.Callback {

	
	private SectionsPagerAdapter sectionsPagerAdapter;
	private ViewPager viewPager;
	private ApplicationController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ArrayList<Track> searchTracks = new ArrayList<Track>();
		searchTracks.add(new Track("test", "testa", "uri"));
		searchTracks.add(new Track("test", "testa", "uri2"));
		ArrayList<Track> playlistTracks = new ArrayList<Track>();
//		playlistTracks.add(new Track("playlistTracks", "testa", "uri"));
//		playlistTracks.add(new Track("playlistTracks", "testa", "uri2"));
		sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), searchTracks, playlistTracks, this);
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(sectionsPagerAdapter);
		controller = new ApplicationController(this, this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search_menu, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
		searchView.setOnQueryTextListener(new QueryTextListener(this));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onSearchResult(ArrayList<Track> tracks) {
		viewPager.setCurrentItem(SectionsPagerAdapter.FIRST_PAGE, true);
		sectionsPagerAdapter.updateResults(tracks);
	}
	
	public void onTrackSelected(Track track) {
		int currentPage = sectionsPagerAdapter.getCurrentPage();
		
		switch (currentPage) {
		case SectionsPagerAdapter.FIRST_PAGE:
			controller.addTrack(track);
			break;
		case SectionsPagerAdapter.SECOND_PAGE:
			
			break;
		}
	}
	
	@Override
	public void onUpdatePlaylist(Track track) {
		sectionsPagerAdapter.updatePlaylist(track);
	}

	public static class DummySectionFragment extends Fragment {

		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() { }

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			TextView textView = new TextView(getActivity());
			textView.setGravity(Gravity.CENTER);
			textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			return textView;
		}
	}
}
