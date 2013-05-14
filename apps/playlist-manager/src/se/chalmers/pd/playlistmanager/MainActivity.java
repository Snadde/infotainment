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

public class MainActivity extends FragmentActivity implements AndroidSpotifyMetadata.Callback {

	
	private SectionsPagerAdapter sectionsPagerAdapter;
	private ApplicationController controller;
	private ArrayList<Track> tracks;
	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tracks = new ArrayList<Track>();
		tracks.add(new Track("test", "testa", "uri"));
		tracks.add(new Track("test", "testa", "uri2"));
		sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), tracks, this);
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(sectionsPagerAdapter);
		controller = new ApplicationController(this);
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
