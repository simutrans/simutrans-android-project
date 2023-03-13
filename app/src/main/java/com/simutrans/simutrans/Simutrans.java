package com.simutrans.simutrans;

import org.libsdl.app.SDLActivity;
import android.os.Bundle;
import android.util.Log;
import android.content.res.AssetManager;
import java.io.*;

/**
* A sample wrapper class that just calls SDLActivity
*/
public class Simutrans extends SDLActivity { 

	@Override
    protected String[] getLibraries() {
        return new String[] {
            "SDL2",
            "simutrans"
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("Simutrans", "Interno: " + this.getFilesDir().getAbsolutePath());
        Log.v("Simutrans", "Externo: " + getExternalFilesDir(null).getAbsolutePath());
        try {
            // TODO: Do this only once at installation
            //copyDirorfileFromAssetManager("", "");
        } catch (Exception e) {
            Log.v("Simutrans", "Error:" + e.getMessage());
        }
        super.onCreate(savedInstanceState);
    }

    public String copyDirorfileFromAssetManager(String arg_assetDir, String arg_destinationDir) throws IOException
    {
        String sd_path = getExternalFilesDir(null).getAbsolutePath();
        String dest_dir_path = sd_path + "/" + arg_destinationDir;
        Log.v("Simutrans", "dest_dir_path: " + dest_dir_path);
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


    public void copyAssetFile(String assetFilePath, String destinationFilePath) throws IOException {
        InputStream in = getApplicationContext().getAssets().open(assetFilePath);
        OutputStream out = new FileOutputStream(destinationFilePath);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
            out.write(buf, 0, len);
        in.close();
        out.close();
    }

    public String addTrailingSlash(String path) {
        if (path.length() > 0 && path.charAt(path.length() - 1) != '/') {
            path += "/";
        }
        return path;
    }

    public void createDir(File dir) throws IOException {
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

} 
