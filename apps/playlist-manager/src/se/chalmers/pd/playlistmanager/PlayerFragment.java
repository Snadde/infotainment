package se.chalmers.pd.playlistmanager;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * This fragment shows the player controls and track information.
 */
public class PlayerFragment extends Fragment implements View.OnClickListener, OnSeekBarChangeListener {

    private static final int DELAY_MILLIS = 1000;
    private View pause;
    private View play;
    private TextView trackInfo;
    private SeekBar seekbar;
    private FragmentCallback callback;
    private ArrayList<Track> tracks;
    private ValueAnimator seekbarAnimator;
    private boolean playing = false;

    public PlayerFragment() {
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
     * Fetches the playlist from the arguments. The playlist is shared with the other
     * fragment so we can't create a new instance. Have to reference the same.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle arguments = getArguments();
        tracks = arguments.getParcelableArrayList("playlist");
    }

    /**
     * Sets up the root view and its buttons.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the root view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player, null);
        setupButtons(rootView);
        seekbar.setOnSeekBarChangeListener(this);
        trackInfo = (TextView) rootView.findViewById(R.id.track_info);
        updateTrackInformation();
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
    }

    /**
     * Handles the clicks on the buttons and calls back to the parent activity
     * the clicked action.
     *
     * @param v the view that was clicked
     */
    @Override
    public void onClick(View v) {
        if (!tracks.isEmpty()) {
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

    /**
     * Called when there has been an incoming message and the UI needs to
     * update. Switches play/pause buttons, handles previous and next and
     * also updates the seek.
     *
     * @param action
     * @param t
     * @param <T>
     */
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
                updateTrackInformation();
                setSeekbarMax();
                startProgress();
                playing = true;
                break;
            case next:
            case prev:
                updateTrackInformation();
                stopProgress();
                resetProgress();
                setSeekbarMax();
                if (playing) {
                    startProgress();
                }
                break;
            case seek:
                float position = (Float) t;
                stopProgress();
                setProgress(((int) (position * seekbar.getMax())));
                if (playing) {
                    startProgress();
                }
                break;
        }
    }

    /**
     * Sets a new max on the seekbar from the first track in the
     * current list of tracks.
     */
    private void setSeekbarMax() {
        if(tracks.size() > 0) {
            Track track = tracks.get(0);
            if (track != null && seekbar != null) {
                seekbar.setMax(track.getLength());
            }
        }
    }

            /**
             * Updates the track information in the text view with
             * the first track in the playlist.
             */
    private void updateTrackInformation() {
        if(tracks.size() > 0) {
            Track track = tracks.get(0);
            if (track != null && trackInfo != null) {
                trackInfo.setText(track.getArtist() + " - " + track.getName());
            }
        }
    }

    /**
     * Stops the seek bar animation
     */
    private void stopProgress() {
        if(seekbarAnimator != null) {
            seekbarAnimator.cancel();
            seekbarAnimator.removeAllUpdateListeners();
        }
    }

    /**
     * Starts the seek bar animation
     */
    private void startProgress() {
        seekbarAnimator = ValueAnimator.ofInt(seekbar.getProgress(), seekbar.getMax());
        seekbarAnimator.setDuration(DELAY_MILLIS * seekbar.getMax());
        seekbarAnimator.setInterpolator(null);
        seekbarAnimator.addUpdateListener(new SeekbarAnimatorUpdateListener());
        seekbarAnimator.start();
    }

    /**
     * Sets the progress when a seek action has been triggered.
     *
     * @param progress the progress to set
     */
    private void setProgress(int progress) {
        if(seekbar != null) {
            seekbar.setProgress(progress);
        }
    }

    /**
     * Resets the progress to 0
     */
    private void resetProgress() {
        setProgress(0);
    }

    /**
     * When the user touches the seekbar we stop progress to prevent jerkiness.
     *
     * @param seekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (playing) {
            stopProgress();
        }
    }

    /**
     * When the user stops touching the seekbar we track the position and tell the
     * parent activity which publishes a message about new the progress.
     *
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        float position = (float) seekBar.getProgress() / seekBar.getMax();
        callback.onPlayerAction(position);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // Not used
    }

    private class SeekbarAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            int animProgress = (Integer) valueAnimator.getAnimatedValue();
            setProgress(animProgress);
        }
    }
}
