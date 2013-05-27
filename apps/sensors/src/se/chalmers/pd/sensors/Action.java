package se.chalmers.pd.sensors;

/**
 * This class contains the available actions that can be performed and sent
 * by the application. They are;
 *
 * <b>action</b>
 * Not a specific action itself. Used to populate the action key.
 *
 * <b>play</b>
 * Used when the music starts or should start playing.
 *
 * <b>pause</b>
 * Used when the music pauses or should pause.
 *
 * <b>next</b>
 * Used when the playlist should skip to the next track.
 *
 * <b>prev</b>
 * Used when the playlist should skip to the previous track.
 *
 * <b>NONE</b>
 * Not used.
 */
public enum Action {
	action,
	play,
	pause,
	next,
    prev,
	NONE
}
