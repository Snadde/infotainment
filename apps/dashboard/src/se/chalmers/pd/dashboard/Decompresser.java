package se.chalmers.pd.dashboard;

import java.io.BufferedInputStream;
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

	private void createBaseDirectory(String directory) {
		File file = new File(location + directory);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
	}
}