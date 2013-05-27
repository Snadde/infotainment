package se.chalmers.pd.playlistmanager;

public interface FragmentCallback {
    public void onPlayerAction(Action action);
    public void onPlayerAction(float data);
    public void onTrackSelected(Track track);
}
