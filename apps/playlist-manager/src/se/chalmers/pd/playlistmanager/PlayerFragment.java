package se.chalmers.pd.playlistmanager;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.util.ArrayList;


public class PlayerFragment extends Fragment implements View.OnClickListener, OnSeekBarChangeListener {

    private static final int MILLIS_IN_SECOND = 1000;
    private static final int DELAY_MILLIS = 1000;
    private View pause;
    private View play;
    private TextView trackInfo;
    private SeekBar seekbar;
    private FragmentCallback callback;
    private ArrayList<Track> tracks;
    private Handler handler = new Handler();
    private boolean playing = false;

    public PlayerFragment() { }


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
        setRetainInstance(true);
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
        if(!tracks.isEmpty()) {
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
    }

    public <T extends Object> void updateAction(Action action, T t) {
        switch (action) {
            case pause:
                stopProgress();
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                playing = false;
                break;
            case play:
                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
                startProgress();
                playing = true;
                break;
            case next:
                updateTrack(tracks.get(0));
                break;
            case prev:
                updateTrack(tracks.get(0));
                break;
            case seek:
                float position = (Float) t;
                setProgress(((int) (position * seekbar.getMax())));
                if(playing) {
                    startProgress();
                }
                break;
        }
    }

    private void updateTrack(Track track) {
        if (track != null && trackInfo != null) {
            trackInfo.setText(track.getArtist() + " - " + track.getName());
            seekbar.setMax(track.getLength());
            resetProgress();
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seekbar.incrementProgressBy((DELAY_MILLIS / MILLIS_IN_SECOND));
            handler.postDelayed(this, DELAY_MILLIS);
        }
    };

    private void stopProgress() {
        handler.removeCallbacks(runnable);
    }

    private void startProgress() {
        handler.postDelayed(runnable, DELAY_MILLIS);
    }

    private void setProgress(int progress) {
        seekbar.setProgress(progress);
    }

    private void resetProgress() {
        setProgress(0);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if(playing) {
            stopProgress();
        }
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
}
