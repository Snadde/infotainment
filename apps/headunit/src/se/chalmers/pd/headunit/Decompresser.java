package se.chalmers.pd.headunit;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;

/**
 * This class decompresses a zip file in a separate thread. If you wish to know
 * when the decompressing has finished you must implement the Callback
 * interface.
 */
public class Decompresser {

	public interface Callbacks {
		/**
		 * Callback from the Decompresser that lets the implementing class know
		 * when the decompressing has finished.
		 * 
		 * @param result
		 *            as a boolean, true if successful
		 * @param privateTopic
		 *            the topic to post result to
		 */
		public void decompressComplete(boolean result, String privateTopic);
	}

	private String location;
	private Callbacks callback;
	private String privateTopic;

	/**
	 * Saves initial data and creates the base directory
	 * 
	 * @param location
	 * @param callback
	 * @param privateTopic
	 */
	public Decompresser(String location, Callbacks callback, String privateTopic) {
		this.location = location;
		this.callback = callback;
		this.privateTopic = privateTopic;
		createBaseDirectory("");
	}

	/**
	 * Spawns a new thread and unzips the data that can be found in the
	 * inputstream
	 * 
	 * @param inputStream
	 */
	public void unzip(final InputStream inputStream) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean result = false;
				try {
					BufferedInputStream bis = new BufferedInputStream(inputStream);
					ZipInputStream zipInputStream = new ZipInputStream(bis);
					ZipEntry zipEntry = null;

					while ((zipEntry = zipInputStream.getNextEntry()) != null) {
						Log.v("Decompresser", "Unzipping " + zipEntry.getName());
						if (zipEntry.isDirectory()) {
							createBaseDirectory(zipEntry.getName());
						} else {
							int readBytes;
							byte byteArray[] = new byte[1024];
							FileOutputStream fileOutputStream = new FileOutputStream(location + zipEntry.getName());
							while ((readBytes = zipInputStream.read(byteArray, 0, 1024)) >= 0) {
								fileOutputStream.write(byteArray, 0, readBytes);
							}
							zipInputStream.closeEntry();
							fileOutputStream.close();
						}
					}
					zipInputStream.close();
					result = true;
				} catch (Exception e) {
					Log.e("Decompresser", "" + e.getMessage());
					result = false;
				}
				callback.decompressComplete(result, privateTopic);
			}
		}).start();
	}

	/**
	 * Helper method to create directories.
	 * 
	 * @param directory
	 */
	private void createBaseDirectory(String directory) {
		File file = new File(location + directory);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
	}
}