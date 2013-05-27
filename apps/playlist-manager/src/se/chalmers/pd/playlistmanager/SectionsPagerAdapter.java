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
    private ArrayList<Track> playlist = new ArrayList<Track>();

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }
    // TODO override life cycle to save string
    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        switch (position) {
            case FIRST_PAGE:
                searchFragment = new TrackListFragment();
                args.putString("title", context.getString(R.string.search_title));
                args.putParcelableArrayList("playlist", new ArrayList<Track>());
                searchFragment.setArguments(args);
                return searchFragment;
            case SECOND_PAGE:
                playlistFragment = new TrackListFragment();
                args.putString("title", context.getString(R.string.playlist_title));
                args.putParcelableArrayList("playlist", playlist);
                playlistFragment.setArguments(args);
                return playlistFragment;
            case THIRD_PAGE:
                playerFragment = new PlayerFragment();
                args.putParcelableArrayList("playlist", playlist);
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

    public void updateSearchResults(ArrayList<Track> newTracksResult) {
        searchFragment.updateResults(newTracksResult);
    }

    public void performAction(Action action) {
        performAction(action, null);
    }

    public void performAction(Action action, Track track) {
        if (playlistFragment != null) {
            playlistFragment.updateAction(action, track);
        }
        if (playerFragment != null) {
            playerFragment.updateAction(action);
        }
    }

    public void resetPlaylist() {
        playlistFragment.resetPlaylist();
    }

    public void seek(float position) {
        if (playerFragment != null) {
            playerFragment.updateSeekbar(position);
        }
    }

}
