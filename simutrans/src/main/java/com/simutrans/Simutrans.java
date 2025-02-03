package com.simutrans;

import android.widget.Toast;

import org.libsdl.app.SDLActivity;
import android.os.Bundle;
import android.util.Log;
import android.content.res.AssetManager;
import java.io.*;
import java.util.Scanner;
import java.net.HttpURLConnection;
import java.net.URL;

/**
* A sample wrapper class that just calls SDLActivity
*/
public class Simutrans extends SDLActivity { 

	static {
		System.loadLibrary("simutrans");
	}

	@Override
	protected String[] getLibraries() {
		return new String[] {
			"SDL2",
			"simutrans"
		};
	}
	
	@Override
		protected String[] getArguments() {
		return new String[] {"-autodpi", "-fullscreen", "-log", "-debug 3"};
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		unpackAssetsIfNeeded();
		super.onCreate(savedInstanceState);
	}

	private void unpackAssetsIfNeeded() {
		String versionFilePath = getFilesDir().getAbsolutePath() + "/config/version";
		if(isInstallOrUpdate(versionFilePath)) { 
			Log.v("Simutrans", "Version update or new install: Unpacking assets");
			try {
				// TODO: Do this every update, not only on first install
				copyDirorfileFromAssetManager("", "");
				FileWriter myWriter = new FileWriter(versionFilePath);
				myWriter.write(getVersion());
				myWriter.close();
			} catch (IOException e) {
				Log.v("Simutrans", "Error unpacking assets:" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private boolean isInstallOrUpdate(String versionFilePath) {
		File versionFile = new File(versionFilePath);
		// get_version() also initialized the internal references on the methods, so do not call it twice ...
		String simuVersion = getVersion();
		Log.v("Simutrans", "Current Version: " + simuVersion );
		try {
			Scanner version = new Scanner(versionFile);
			if ( version.hasNextLine() && version.nextLine().equals(simuVersion)  ) {
				return false;
			}
			return true;
		} catch (FileNotFoundException e) {
			try {
				versionFile.createNewFile();
			}
			catch (IOException ioe) {
				Log.v("Simutrans", "Can't create version file:" + ioe.getMessage());
			}
		}
		return true;
	}

	private String copyDirorfileFromAssetManager(String arg_assetDir, String arg_destinationDir) throws IOException
	{
		String sd_path = getFilesDir().getAbsolutePath();
		String dest_dir_path = sd_path + "/" + arg_destinationDir;
		File dest_dir = new File(dest_dir_path);

		createDir(dest_dir);

		AssetManager asset_manager = getApplicationContext().getAssets();
		String[] files = asset_manager.list(arg_assetDir);

		for (int i = 0; i < files.length; i++) {

			String abs_asset_file_path = addTrailingSlash(arg_assetDir) + files[i];
			String sub_files[] = asset_manager.list(abs_asset_file_path);

			if (sub_files.length == 0) {
				// It is a file
				String dest_file_path = addTrailingSlash(dest_dir_path) + files[i];
				copyAssetFile(abs_asset_file_path, dest_file_path);
			} else {
				// It is a sub directory
				copyDirorfileFromAssetManager(abs_asset_file_path, addTrailingSlash(arg_destinationDir) + files[i]);
			}
		}

		return dest_dir_path;
	}


	private void copyAssetFile(String assetFilePath, String destinationFilePath) throws IOException {
		InputStream in = getApplicationContext().getAssets().open(assetFilePath);
		OutputStream out = new FileOutputStream(destinationFilePath);

		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0)
			out.write(buf, 0, len);
		in.close();
		out.close();
	}

	private String addTrailingSlash(String path) {
		if (path.length() > 0 && path.charAt(path.length() - 1) != '/') {
			path += "/";
		}
		return path;
	}

	private void createDir(File dir) throws IOException {
		if (dir.exists()) {
			if (!dir.isDirectory()) {
				throw new IOException("Can't create directory, a file is in the way");
			}
		} else {
			dir.mkdirs();
			if (!dir.isDirectory()) {
				throw new IOException("Unable to create directory");
			}
		}
	}
	
	private native String getVersion();

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.exit(0); // Otherwise Simutrans will not be able to initialize static objects at next start
	}

	static public void downloadFile(String fileURL,String filename) throws IOException {
		URL url = new URL(fileURL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();

		// Check if the response code is HTTP OK (200)
		if (responseCode == HttpURLConnection.HTTP_OK) {
			// Open input stream from the HTTP connection
			InputStream inputStream = httpConn.getInputStream();
			
			// Open output stream to save file
			FileOutputStream outputStream = new FileOutputStream(filename);

			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			byte[] buffer = new byte[4096];
			int bytesRead = -1;

			while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			outputStream.close();
			bufferedInputStream.close();
			httpConn.disconnect();
		}
		else {
			throw new IOException("Server error " + responseCode);
		}
	}
} 
