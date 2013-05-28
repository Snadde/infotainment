package se.chalmers.pd.playlistmanager;
/**
 * This class contains the available actions that can be performed and sent
 * by the application. They are;
 *
 * <b>action</b>
 * Not a specific action itself. Used to populate the action key.
 *
 * <b>add</b>
 * Used when a track should be added to the playlist.
 *
 * <b>add_all</b>
 * Used when a full playlist is added.
 *
 * <b>get_all</b>
 * Used when a full playlist needs to be fetched.
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
 * <b>seek</b>
 * Used when the track should seek and update its scrubber.
 *
 * <b>NONE</b>
 * Not used.
 */

public enum Action {
	action,
	add,
	add_all,
	get_all,
	play,
	next,
	pause,
	prev,
	seek,
	NONE
}
