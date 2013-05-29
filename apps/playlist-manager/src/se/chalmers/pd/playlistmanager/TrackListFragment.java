package se.chalmers.pd.playlistmanager;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This fragment is used to show list of tracks. Both the playlist and the search
 * result list make use of this fragment.
 */
public class TrackListFragment extends ListFragment {

    private String title;
    private TrackAdapter adapter;
    private FragmentCallback callback;

    /**
     * Empty constructor used by the system
     */
    public TrackListFragment() {
    }

    /**
     * Saves the activity as a callback
     *
     * @param activity
     * @throws ClassCastException if the activity doesn't implement the FragmentCallback
     */
    @Override
    public void onAttach(Activity activity) throws ClassCastException {
        super.onAttach(activity);
        try {
            callback = (FragmentCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement FragmentCallback");
        }
    }

    /**
     * Gets the tracks from the passed in arguments and sets the list to the adapter.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString("title", "");
        ArrayList<Track> tracks = getArguments().getParcelableArrayList("playlist");
        setupAdapter(tracks);
    }

    /**
     * Sets the title of the fragment and returns the root view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the root view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracklist, null);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(title);
        return view;
    }

    /**
     * When a list item has been clicked the track is passed on to the callback
     *
     * @param l        listview
     * @param v        view
     * @param position position in list
     * @param id
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Track track = (Track) getListAdapter().getItem(position);
        callback.onTrackSelected(track);
    }

    /**
     * Updates the tracks in the list
     *
     * @param tracks the tracks to use
     */
    public void updateTracks(ArrayList<Track> tracks) {
        setupAdapter(tracks);
    }

    /**
     * Sets up the adapter with the tracks and the system provided layout.
     *
     * @param tracks the tracks to add to the list
     */
    private void setupAdapter(final ArrayList<Track> tracks) {
        adapter = new TrackAdapter(getActivity(), android.R.layout.simple_list_item_2, tracks);
        setListAdapter(adapter);
    }

    /**
     * Called when a message has been received or an action needs to be performed to
     * update the UI.
     *
     * @param action the action to perform
     * @param t      the data to use with the action (null if none)
     */
    public <T extends Object> void updateAction(Action action, T t) {
        switch (action) {
            case add:
                addTrack((Track) t);
                break;
            case add_all:
                addAllTracks((ArrayList<Track>) t);
                break;
            case next:
                shiftNext();
                break;
            case prev:
                shiftPrev();
                break;
        }
    }

    /**
     * Adds a single track to the list
     * @param track the track to add
     */
    private void addTrack(Track track) {
        adapter.add(track);
    }

    /**
     * Clears the adapter and adds all tracks to it
     * @param list
     */
    private void addAllTracks(ArrayList<Track> list) {
        adapter.clear();
        for (Track track : list) {
            addTrack(track);
        }
    }

    /**
     * Shifts the list forward taking the first element and putting it last.
     */
    private void shiftNext() {
        Track originalTrack = adapter.getItem(0);
        adapter.remove(originalTrack);
        adapter.add(originalTrack);
        adapter.notifyDataSetChanged();
    }

    /**
     * Shifts the list backward, taking the last element and placing it first.
     */
    private void shiftPrev() {
        Track originalTrack = adapter.getItem(adapter.getCount() - 1);
        adapter.remove(originalTrack);
        adapter.insert(originalTrack, 0);
        adapter.notifyDataSetChanged();
    }
}
