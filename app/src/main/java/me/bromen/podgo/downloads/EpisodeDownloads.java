package me.bromen.podgo.downloads;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.esafirm.rxdownloader.RxDownloader;

import java.io.Serializable;
import java.util.HashMap;

import me.bromen.podgo.structures.FeedItem;
import me.bromen.podgo.utilities.PodcastFileUtils;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jeff on 5/19/17.
 */

public class EpisodeDownloads implements Serializable {

    private final Context context;
    private final DownloadManager downloadManager;
    private HashMap<String, Long> activeDownloads = new HashMap<>();

    public EpisodeDownloads(Context context) {
        this.context = context;
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public void startDownload(FeedItem item) {

        long reference;

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(item.getEnclosure().getUrl()));

        request.setTitle("PodGo");
        request.setDescription(item.getTitle());

        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_PODCASTS,
                PodcastFileUtils.sanitizeName(item.getTitle() + " " + item.getId()) + ".mp3");

        RxDownloader.getInstance(context)
                .download(request)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(path -> {

                }, throwable -> {

                });

        reference = downloadManager.enqueue(request);

        activeDownloads.put(item.getTitle() + " " + item.getId(), reference);
    }

    public boolean isDownloading(FeedItem item) {

        if (!activeDownloads.containsKey(item.getTitle() + " " + item.getId())) {
            return false;
        }

        long reference = activeDownloads.get(item.getTitle() + " " + item.getId());

        return isDownloading(reference);
    }

    public boolean isDownloading(long reference) {
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(reference);

        Cursor cursor = dm.query(query);

        return cursor.moveToFirst();
    }

    public void validateDownloads() {

        for (long reference : activeDownloads.values()) {
            if (!isDownloading(reference)) {
                activeDownloads.values().remove(reference);
            }
        }
    }

    public void cancelDownload(FeedItem item) {

        if (activeDownloads.containsKey(item.getTitle() + " " + item.getId())) {

            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            dm.remove(activeDownloads.get(item.getTitle() + " " + item.getId()));
        }
    }

    public void completeDownload(long reference) {
        activeDownloads.values().remove(reference);
    }
}
