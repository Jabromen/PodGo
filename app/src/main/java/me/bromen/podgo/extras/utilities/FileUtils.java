package me.bromen.podgo.extras.utilities;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by jeff on 5/6/17.
 */

public class FileUtils {

    private FileUtils() {}

    public static String getFullAudioFilePath(Context context, String filename) {
        return new File(context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS), filename).getPath();
    }

    public static boolean fileExists(String filename) {
        return new File(filename).exists();
    }

    public static String sanitizeName(String name) {
        return name.replaceAll("[|#%&\\?*<\":>+/']", "_");
    }
}


