package me.bromen.podgo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.Log;

import com.icosillion.podengine.exceptions.MalformedFeedException;
import com.icosillion.podengine.models.Podcast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

/**
 * Created by jeff on 5/6/17.
 */

class PodcastFileUtils {

    static void savePodcastInfo(Context context, Podcast podcast) {
        String url = podcast.getFeedURL().toString();
        String xml = podcast.getXMLData();
        String title = null;
        try {
            title = podcast.getTitle();
        } catch (MalformedFeedException e) {
            e.printStackTrace();
        }

        if (title != null) {
            File file = getPodcastStorageDir(context, title);
            try {
                File xmlFile = new File(file.getPath() + "/feed.xml");
                FileUtils.writeStringToFile(xmlFile, xml, Charset.forName("UTF-8"));

                File urlFile = new File(file.getPath() + "/url.txt");
                FileUtils.writeStringToFile(urlFile, url, Charset.forName("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (File f:file.listFiles()) {
                Log.e("PodcastFileUtils", f.getPath());
            }
        }
    }

    static File getPodcastImageFile(Context context, String title) {
        File dir = getPodcastStorageDir(context, title);
        return new File(dir, "podcastImage.jpg");
    }

    static boolean imageFileIsSaved(Context context, String title) {
        return getPodcastImageFile(context, title).exists();
    }

    static File getPodcastStorageDir(Context context, String title) {
        File podcastDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS), sanitizeName(title));

        if (!podcastDir.exists()) {
            podcastDir.mkdirs();
        }

        return podcastDir;
    }

    static File getEpisodeAudioFile(Context context, String podcastTitle, String episodeTitle) {
        File podcastDir = getPodcastStorageDir(context, podcastTitle);

        return new File(podcastDir, sanitizeName(episodeTitle) + ".mp3");
    }

    static boolean isEpisodeDownloaded(Context context, String podcastTitle, String episodeTitle) {
        return getEpisodeAudioFile(context, podcastTitle, episodeTitle).exists();
    }

    static Podcast loadPodcastFromFile(Context context, String title) {
        File dir = getPodcastStorageDir(context, title);

        File xmlFile = new File(dir, "feed.xml");
        File urlFile = new File(dir, "url.txt");
        if (!xmlFile.exists() || !urlFile.exists())
            return null;
        try {
            String xml = FileUtils.readFileToString(xmlFile, Charset.forName("UTF-8"));
            String url = FileUtils.readFileToString(urlFile, Charset.forName("UTF-8"));

            return new Podcast(xml, new URL(url));

        } catch (IOException | MalformedFeedException e) {
            e.printStackTrace();
            return null;
        }
    }

    static void deletePodcast(Context context, String podcastTitle) {
        File podcastDir = getPodcastStorageDir(context, podcastTitle);

        try {
            FileUtils.deleteDirectory(podcastDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String sanitizeName(String name) {
        return name.replaceAll("[|#%&\\?*<\":>+/']", "_");
    }
}


