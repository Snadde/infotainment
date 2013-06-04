package se.chalmers.pd.playlistmanager;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * This is the adapter that controls the view pager and which fragments are shown. It
 * also functions as a communication middle hand between the activity and the fragments.
 */
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

    /**
     * Constructor which calls super with the fragment manager and saves the context for
     * later use.
     *
     * @param fm      the fragment manager available
     * @param context the context to use the adapter in
     */
    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    /**
     * Decides which fragment will be shown when the view pager is scrolled.
     *
     * @param position the position of the fragment
     * @return the fragment to show.
     */
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

    /**
     * Returns the number of pages in the adapter
     *
     * @return total pages in the adapter
     */
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

    /**
     * Called when the search results need to update in the search fragment.
     *
     * @param newTracksResult the new results
     */
    public void updateSearchResults(ArrayList<Track> newTracksResult) {
        searchFragment.updateTracks(newTracksResult);
    }

    /**
     * Calls the fragments update action methods which use the action to decided what
     * each fragment needs to update. Use this if there is no extra data required
     * for your action.
     *
     * @param action one of next, prev, play, pause
     */
    public void performAction(Action action) {
        performAction(action, null);
    }

    /**
     * Calls the fragments with actions and the extra data they might need.
     *
     * @param action the action to perform
     * @param t      the extra data, null if not needed
     */
    public <T extends Object> void performAction(Action action, T t) {
        if (playlistFragment != null) {
            playlistFragment.updateAction(action, t);
        }
        if (playerFragment != null) {
            playerFragment.updateAction(action, t);
        }
    }

}
