package me.bromen.podgo;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by jeff on 5/19/17.
 */

public class EpisodeDownloads implements Serializable {

    private HashMap<String, Long> activeDownloads = new HashMap<>();

    public void startDownload(Context context, Uri uri, String podcastTitle, String episodeTitle) {

        long reference;

        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setTitle("PodGo");
        request.setDescription(podcastTitle + " - " + episodeTitle);

        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_PODCASTS,
                "/" + PodcastFileUtils.sanitizeName(podcastTitle) +
                        "/" + PodcastFileUtils.sanitizeName(episodeTitle) + ".mp3");

        reference = dm.enqueue(request);

        activeDownloads.put(podcastTitle + " " + episodeTitle, reference);
    }

    public boolean isDownloading(Context context, String podcastTitle, String episodeTitle) {

        if (!activeDownloads.containsKey(podcastTitle + " " + episodeTitle)) {
            return false;
        }

        long reference = activeDownloads.get(podcastTitle + " " + episodeTitle);

        return isDownloading(context, reference);
    }

    public boolean isDownloading(Context context, long reference) {
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(reference);

        Cursor cursor = dm.query(query);

        return cursor.moveToFirst();
    }

    public void validateDownloads(Context context) {

        for (long reference : activeDownloads.values()) {
            if (!isDownloading(context, reference)) {
                activeDownloads.values().remove(reference);
            }
        }
    }

    public void cancelDownload(Context context, String podcastTitle, String episodeTitle) {

        if (activeDownloads.containsKey(podcastTitle + " " + episodeTitle)) {

            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            dm.remove(activeDownloads.get(podcastTitle + " " + episodeTitle));
        }
    }

    public void completeDownload(long reference) {
        activeDownloads.values().remove(reference);
    }
}
