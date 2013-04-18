package se.chalmers.pd.dashboard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;

public class Decompresser {
	
	public interface Callbacks {
		public void decompressComplete(boolean result, String privateTopic);
	} 

	private String location;
	private Callbacks callback;
	private String privateTopic;

	public Decompresser(String location, Callbacks callback, String privateTopic) {
		this.location = location;
		this.callback = callback;
		this.privateTopic = privateTopic;
		createBaseDirectory("");
	}

	public void unzip(final InputStream inputStream) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean result = false;
				try {
					ZipInputStream zipInputStream = new ZipInputStream(inputStream); 
					ZipEntry zipEntry = null;
					
					while ((zipEntry = zipInputStream.getNextEntry()) != null) {
						Log.v("Decompresser", "Unzipping " + zipEntry.getName());
						if (zipEntry.isDirectory()) {
							createBaseDirectory(zipEntry.getName());
						} else {
							FileOutputStream fileOutputStream = new FileOutputStream(location + zipEntry.getName());
							for (int readByte = zipInputStream.read(); readByte != -1; readByte = zipInputStream.read()) {
								fileOutputStream.write(readByte);
							}
							zipInputStream.closeEntry();
							fileOutputStream.close();
						}
					}
					zipInputStream.close();
					result = true;
				} catch (Exception e) {
					Log.e("Decompresser", e.getMessage());
					result = false;
				}
				callback.decompressComplete(result, privateTopic);
			}
		}).start();
	}

	private void createBaseDirectory(String directory) {
		File file = new File(location + directory);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
	}
}