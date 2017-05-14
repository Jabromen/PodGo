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

    static void savePodcastImage(Context context, String podcast, Bitmap image, boolean overWriteFile) {

        OutputStream os = null;

        File dir = getPodcastStorageDir(context, podcast);
        File imageFile = new File(dir, "podcastImage.png");

        if (imageFile.exists()) {
            if (overWriteFile) {
                imageFile.delete();
                imageFile = new File(dir, "podcastImage.png");
            }
            else {
                return;
            }
        }
        try {
            os = new FileOutputStream(imageFile);
            image.compress(Bitmap.CompressFormat.PNG, 10, os);
            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getPodcastImage(Context context, String title) {
        File dir = getPodcastStorageDir(context, title);
        File image = new File(dir, "podcastImage.png");

        if (image.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(image.getPath(), options), 150, 150);
        }
        else {
            return null;
        }
    }

    public static File getPodcastStorageDir(Context context, String title) {
        File podcastDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS), title);

        if (!podcastDir.exists()) {
            podcastDir.mkdirs();
        }

        return podcastDir;
    }

    public static File getEpisodeAudioFile(Context context, String podcastTitle, String episodeTitle) {
        File podcastDir = getPodcastStorageDir(context, podcastTitle);

        return new File(podcastDir, episodeTitle + ".mp3");
    }

    public static boolean isEpisodeDownloaded(Context context, String podcastTitle, String episodeTitle) {
        return getEpisodeAudioFile(context, podcastTitle, episodeTitle).exists();
    }

    public static Podcast loadPodcastFromFile(Context context, String title) {
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

    public static void deletePodcast(Context context, String podcastTitle) {
        File podcastDir = getPodcastStorageDir(context, podcastTitle);

        try {
            FileUtils.deleteDirectory(podcastDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


