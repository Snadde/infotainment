package se.chalmers.pd.device;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.util.Base64;


/**
 * Helper class to convert inputstreams or assets into base64 strings.
 */
public class StreamToBase64String {
	private static StreamToBase64String streamToBase64String;
	private static Context context;

	private StreamToBase64String() {
		
	}
	
	/**
	 * Retrives the instance of this object.
	 * @return the instance
	 */
	public static StreamToBase64String getInstance(Context ctx) {
		if(streamToBase64String == null) {
			streamToBase64String = new StreamToBase64String();
		}
		context = ctx;
		return streamToBase64String;
	}
	
	/**
	 * Finds an asset in the asset folder with the filename specified
	 * and creates a base64 string from it.
	 * @param filename
	 * @return the base64 representation of the file
	 */
	public String getBase64StringFromAssets(String filename) {
		InputStream is = getInputStream(filename);
		String base64String = getBase64StringFromStream(is);
		return base64String;
	}
	
	/**
	 * Takes the inputstream given and creates a base64 string from it.
	 * @param filename
	 * @return the base64 representation of the stream
	 */
	public String getBase64StringFromStream(InputStream is) {
		int readBytes;
		int bufferSize = 1024;
		byte byteArray[] = new byte[bufferSize];
		ByteArrayOutputStream ba = new ByteArrayOutputStream();
		try {
			while ((readBytes = is.read(byteArray, 0, bufferSize)) >= 0) {
				ba.write(byteArray, 0, readBytes);
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return Base64.encodeToString(ba.toByteArray(), Base64.DEFAULT);
	}
	
	/**
	 * Helper method to get the inputstream from the assets.
	 * @param filename
	 * @return
	 */
	private InputStream getInputStream(String filename) {
		InputStream inputStream = null;
		try {
			inputStream = context.getAssets().open(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputStream;
	}
	
}
