package se.chalmers.pd.client;

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.util.Log;

public class Decompresser {
	private String zipFile;
	private String location;
	private Context context;

	public Decompresser(String zipFile, String location, Context context) {
		this.zipFile = zipFile;
		this.location = location;
		this.context = context;
		createBaseDirectory("");
	}

	public boolean unzip() {
		boolean result = false;
		try {
			ZipInputStream zipInputStream = new ZipInputStream(context.getAssets().open(zipFile)); 
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