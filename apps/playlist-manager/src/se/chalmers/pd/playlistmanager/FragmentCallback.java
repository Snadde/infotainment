package se.chalmers.pd.playlistmanager;

/**
 * This interface contains callbacks from the fragments.
 */
public interface FragmentCallback {
    /**
     * Called when the user has performed an action on the player such as;
     * next, prev, pause, play
     *
     * @param action the action performed
     */
    public void onPlayerAction(Action action);

    /**
     * Called when the user has performed an action that contains extra integer data
     * such as 'seek'
     *
     * @param data the number related to the action
     */
    public void onPlayerAction(float data);

    /**
     * Called when a track has been selected in a list.
     *
     * @param track the track selected
     */
    public void onTrackSelected(Track track);
}
