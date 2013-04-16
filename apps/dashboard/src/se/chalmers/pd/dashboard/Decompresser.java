package se.chalmers.pd.dashboard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;

public class Decompresser {

	private String location;

	public Decompresser(String location) {
		this.location = location;
		createBaseDirectory("");
	}

	public boolean unzip(InputStream inputStream) {
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
		return result;
	}

	private void createBaseDirectory(String directory) {
		File file = new File(location + directory);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
	}
}