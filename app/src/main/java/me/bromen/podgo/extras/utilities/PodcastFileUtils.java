package me.bromen.podgo.extras.utilities;

import android.content.Context;
import android.os.Environment;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by jeff on 5/6/17.
 */

public class PodcastFileUtils {

    public static File getPodcastImageFile(Context context, String title) {
        File dir = getPodcastStorageDir(context, title);
        return new File(dir, "podcastImage.jpg");
    }

    public static boolean imageFileIsSaved(Context context, String title) {
        return getPodcastImageFile(context, title).exists();
    }

    public static File getPodcastStorageDir(Context context, String title) {
        File podcastDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS), sanitizeName(title));

        if (!podcastDir.exists()) {
            podcastDir.mkdirs();
        }

        return podcastDir;
    }

    public static File getEpisodeAudioFile(Context context, String podcastTitle, String episodeTitle) {
        File podcastDir = getPodcastStorageDir(context, podcastTitle);

        return new File(podcastDir, sanitizeName(episodeTitle) + ".mp3");
    }

    public static boolean isEpisodeDownloaded(Context context, String podcastTitle, String episodeTitle) {
        return getEpisodeAudioFile(context, podcastTitle, episodeTitle).exists();
    }

    public static void deletePodcast(Context context, String podcastTitle) {
        File podcastDir = getPodcastStorageDir(context, podcastTitle);

        try {
            FileUtils.deleteDirectory(podcastDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFullAudioFilePath(Context context, String filename) {
        return new File(context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS), filename).getPath();
    }

    public static boolean isAudioDownloaded(String filename) {
        return new File(filename).exists();
    }

    public static String sanitizeName(String name) {
        return name.replaceAll("[|#%&\\?*<\":>+/']", "_");
    }
}


