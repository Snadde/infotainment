package se.chalmers.pd.playlistmanager;

import java.util.ArrayList;

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
	private Context context;
	private PlayerFragment playerFragment;
	private Track firstTrack;

	public SectionsPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position) {
		Bundle args = new Bundle();
		switch (position) {
		case FIRST_PAGE:
			searchFragment = new TrackListFragment();
			args.putString("title", context.getString(R.string.search_title));
			searchFragment.setArguments(args);
			return searchFragment;
		case SECOND_PAGE:
			playlistFragment = new TrackListFragment();
			args.putString("title", context.getString(R.string.playlist_title));
			playlistFragment.setArguments(args);
			return playlistFragment;
		case THIRD_PAGE:
			playerFragment = new PlayerFragment();
			args.putParcelable("track", firstTrack);
			playerFragment.setArguments(args);
			return playerFragment;
		}
		return null;
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

	public void addToPlaylist(Track track) {
		playlistFragment.addToPlaylist(track);
	}

	public void performAction(Action action) {
		switch (action) {
		case next:
		case prev:
			playlistFragment.updateAction(action);
			break;
		case play:
		case pause:
			playerFragment.updateAction(action);
			break;
		default:
			break;
		}
	}

	public void resetPlaylist() {
		playlistFragment.resetPlaylist();
	}

	public void updatePlayer(Track track) {
		if (firstTrack == null) {
			firstTrack = track;
		} 
		if (playerFragment != null && firstTrack != null) {
			playerFragment.updateTrack(firstTrack);
		}
	}

}
