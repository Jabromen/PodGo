package me.bromen.podgo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

class PodcastSaver {

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
                Log.e("PodcastSaver", f.getPath());
            }
        }
    }

    static void savePodcastImage(Context context, String podcast, Bitmap image, boolean overWriteFile) {

        OutputStream os = null;

        File dir = getPodcastStorageDir(context, podcast);
        File imageFile = new File(dir.getPath() + "/podcastImage.png");

        if (imageFile.exists()) {
            if (overWriteFile) {
                imageFile.delete();
                imageFile = new File(dir.getPath() + "/podcastImage.png");
            }
            else {
                return;
            }
        }
        try {
            os = new FileOutputStream(imageFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, os);
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
        File image = new File(dir.getPath() + "/podcastImage.png");

        if (image.exists()) {
            return BitmapFactory.decodeFile(image.getPath());
        }
        else {
            return null;
        }
    }

    private static File getPodcastStorageDir(Context context, String title) {
        return new File(context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS), title);
    }

    public static Podcast loadPodcastFromFile(Context context, String title) {
        File file = getPodcastStorageDir(context, title);

        File xmlFile = new File(file.getPath() + "/feed.xml");
        File urlFile = new File(file.getPath() + "/url.txt");
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
}


