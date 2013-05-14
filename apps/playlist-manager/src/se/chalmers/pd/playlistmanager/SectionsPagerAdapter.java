package se.chalmers.pd.playlistmanager;

import java.util.ArrayList;

import se.chalmers.pd.playlistmanager.MainActivity.DummySectionFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

	public static final int FIRST_PAGE = 0;
	public static final int SECOND_PAGE = 1;
	public static final int THIRD_PAGE = 2;
	public static final int TOTAL_PAGES = 3;
	
	private TrackListFragment playlistFragment;
	private TrackListFragment searchFragment;
	private ArrayList<Track> searchTracks;
	private ArrayList<Track> playlistTracks;
	private Context context;
	
	
	public SectionsPagerAdapter(FragmentManager fm, ArrayList<Track> searchTracks, ArrayList<Track> playlistTracks, Context context) {
		super(fm);
		this.searchTracks = searchTracks;
		this.playlistTracks = playlistTracks;
		this.context = context;	
	}

	@Override
	public Fragment getItem(int position) {
		Fragment f = new Fragment();
		Bundle args = new Bundle();
		switch (position) {
		case FIRST_PAGE:
			searchFragment = new TrackListFragment();
			args.putString("title", context.getString(R.string.search_title));
			args.putParcelableArrayList("tracks", searchTracks);
			searchFragment.setArguments(args);
			return searchFragment;
		case SECOND_PAGE:
			playlistFragment = new TrackListFragment();
			args.putString("title", context.getString(R.string.playlist_title));
			args.putParcelableArrayList("tracks", playlistTracks);
			playlistFragment.setArguments(args);
			return playlistFragment;
		case THIRD_PAGE:
			f = new DummySectionFragment();
			break;
		}
		f.setArguments(args);
		return f;
	}

	@Override
	public int getCount() {
		return TOTAL_PAGES;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case FIRST_PAGE:
			return context.getString(R.string.title_section1).toUpperCase();
		case SECOND_PAGE:
			return context.getString(R.string.title_section2).toUpperCase();
		case THIRD_PAGE:
			return context.getString(R.string.title_section3).toUpperCase();
		}
		return null;
	}

	public void updateResults(ArrayList<Track> newTracksResult) {
		searchFragment.updateResults(newTracksResult);
	}

	public void updatePlaylist(Track track) {
		playlistFragment.updatePlaylist(track);
	}

}
