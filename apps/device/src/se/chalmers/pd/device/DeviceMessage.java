package se.chalmers.pd.device;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Singleton class to generate system messages for the device.
 *
 */
public class DeviceMessage {
	private static String appUrl = "http://infotainment.danielkvist.net/webapp.zip";
	private DeviceMessage deviceMessage;

	private DeviceMessage() {
		
	}
	
	/**
	 * Retrives the instance of this object.
	 * @return the instance
	 */
	public DeviceMessage getInstance() {
		if(deviceMessage == null) {
			deviceMessage = new DeviceMessage();
		}
		return deviceMessage;
	}
	
	/**
	 * Returns an install message for the given appname
	 * @param appName
	 * @return the message object as a string
	 */
	public static String installMessage(String data) {
		JSONObject json = new JSONObject();
		try {
			json.put("action", "install");
			json.put("data", data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	/**
	 * Returns an install via URL message for the given appname
	 * @param appName
	 * @return the message object as a string
	 */
	public static String installFromUrlMessage(String appName) {
		JSONObject json = new JSONObject();
		try {
			json.put("action", "install-url");
			json.put("data", appUrl );
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	/**
	 * Returns a start message for the given app name
	 * @param appName
	 * @return the message object as a string
	 */
	public static String startMessage(String appName) {
		JSONObject json = new JSONObject();
		try {
			json.put("action", "start");
			json.put("data", appName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	/**
	 * Returns a data message containing the given json data
	 * @param appName
	 * @return the message object as a string
	 */
	public static String dataMessage(JSONObject jsonData) {
		JSONObject json = new JSONObject();
		try {
			json.put("action", "data");
			json.put("data", jsonData);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	/**
	 * Returns a stop message for the given app name
	 * @param appName
	 * @return the message object as a string
	 */
	public static String stopMessage(String appName) {
		JSONObject json = new JSONObject();
		try {
			json.put("action", "stop");
			json.put("data", appName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	/**
	 * Returns an app exist message for the given app name
	 * @param appName
	 * @return the message object as a string
	 */
	public static String existMessage(String appName) {
		JSONObject json = new JSONObject();
		try {
			json.put("action", "exist");
			json.put("data", appName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	/**
	 * Returns an uninstall message for the given appname
	 * @param appName
	 * @return the message object as a string
	 */
	public static String unInstallMessage(String appName) {
		JSONObject json = new JSONObject();
		try {
			json.put("action", "uninstall");
			json.put("data", appName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
}
