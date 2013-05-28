package se.chalmers.pd.headunit;

/**
 * This class contains the available actions that can be performed and sent
 * by the application. They are;
 *
 * <b>action</b>
 * Not a specific action itself. Used to populate the action key.
 *
 * <b>exists</b>
 * Used when a device asks the head unit if an application is
 * installed or not.
 *
 * <b>install</b>
 * Used when a device wants to install an application.
 *
 * <b>uninstall</b>
 * Used when a device wants to uninstall an application.
 *
 * <b>start</b>
 * Used to start the hosted web application.
 *
 * <b>stop</b>
 * Used to stop the hosted web application.
 *
 * <b>NONE</b>
 * Not used.
 */
public enum Action {
    action,
    exist,
    install,
    uninstall,
    start,
    stop,
    NONE
}
