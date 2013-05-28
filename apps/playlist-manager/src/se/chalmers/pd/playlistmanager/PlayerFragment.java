package se.chalmers.pd.playlistmanager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class PlayerFragment extends Fragment implements View.OnClickListener, OnSeekBarChangeListener {

    private int currentIndex = 0;
    private View pause;
    private View play;
    private TextView trackInfo;
    private SeekBar seekbar;
    private FragmentCallback callback;
    private ArrayList<Track> tracks;


    // TODO enable scrubber animation

    public PlayerFragment() {
    }

    ;

    @Override
    public void onAttach(Activity activity) throws ClassCastException {
        super.onAttach(activity);
        try {
            callback = (FragmentCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement FragmentCallback");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        tracks = arguments.getParcelableArrayList("playlist");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player, null);
        setupButtons(rootView);
        return rootView;

    }

    /**
     * Sets up all the buttons for the view and adds onclick listeners.
     */
    private void setupButtons(View rootView) {
        View previous = rootView.findViewById(R.id.prev);
        View next = rootView.findViewById(R.id.next);
        play = rootView.findViewById(R.id.play);
        pause = rootView.findViewById(R.id.pause);
        seekbar = (SeekBar) rootView.findViewById(R.id.seekbar);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        seekbar.setOnSeekBarChangeListener(this);
        trackInfo = (TextView) rootView.findViewById(R.id.track_info);
    }

    @Override
    public void onClick(View v) {
        Action action = Action.NONE;
        switch (v.getId()) {
            case R.id.play:
                action = Action.play;
                break;
            case R.id.next:
                action = Action.next;
                break;
            case R.id.pause:
                action = Action.pause;
                break;
            case R.id.prev:
                action = Action.prev;
                break;
        }
        callback.onPlayerAction(action);
    }

    public void updateAction(Action action) {
        switch (action) {
            case pause:
            case play:
                togglePlayPause();
                break;
            case next:
                //currentIndex = ++currentIndex % tracks.size();
                updateTrack(tracks.get(0));
                break;
            case prev:
                //currentIndex = --currentIndex % tracks.size();
                updateTrack(tracks.get(0));
                break;
        }
    }

    private void togglePlayPause() {
        if (play.getVisibility() == View.VISIBLE) {
            pause.setVisibility(View.VISIBLE);
            play.setVisibility(View.GONE);
        } else {
            pause.setVisibility(View.GONE);
            play.setVisibility(View.VISIBLE);
        }
    }

    private void updateTrack(Track track) {
        if (track != null) {
            trackInfo.setText(track.getArtist() + " - " + track.getName());
        }
    }

    public void updateSeekbar(float position) {
        seekbar.setProgress((int) (position * seekbar.getMax()));
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        float position = (float) seekBar.getProgress() / seekBar.getMax();
        callback.onPlayerAction(position);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // Not used
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Not used
    }
}
