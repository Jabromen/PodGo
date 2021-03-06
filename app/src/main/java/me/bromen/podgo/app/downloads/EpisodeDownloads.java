package me.bromen.podgo.app.downloads;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import me.bromen.podgo.app.storage.DbHelper;
import me.bromen.podgo.extras.structures.FeedItem;
import me.bromen.podgo.extras.utilities.FileUtils;

/**
 * Created by jeff on 5/19/17.
 */

public class EpisodeDownloads {

    private final Context context;
    private final DownloadManager downloadManager;
    private final DbHelper dbHelper;

    private final BroadcastReceiver downloadReceiver;

    private final PublishSubject<Boolean> downloadObservable = PublishSubject.create();

    public EpisodeDownloads(Context context, DbHelper dbHelper) {
        this.context = context;
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        this.dbHelper = dbHelper;

        downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                completeDownload(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1));
                downloadObservable.onNext(true);
            }
        };
    }

    public Observable<Boolean> getDownloadObservable() {
        return this.downloadObservable;
    }

    public void startDownload(FeedItem item) {

        long reference;

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(item.getEnclosure().getUrl()));

        request.setTitle(item.getTitle());
        request.setDescription("PodGo");

        String filename = FileUtils.sanitizeName(item.getTitle() + "-" + item.getId()) + ".mp3";

        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_PODCASTS,
                filename);

        reference = downloadManager.enqueue(request);

        dbHelper.saveDownloading(item.getId(), reference);
        dbHelper.saveStorage(item.getId(), FileUtils.getFullAudioFilePath(context, filename));
    }

    private boolean isDownloading(long reference) {

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(reference);

        Cursor cursor = downloadManager.query(query);

        if (cursor.moveToFirst()) {
            if (cursor.getCount() > 0) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                if (status == DownloadManager.STATUS_RUNNING || status == DownloadManager.STATUS_PENDING ||
                        status == DownloadManager.STATUS_PAUSED) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isDownloadSuccess(long reference) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(reference);

        Cursor cursor = downloadManager.query(query);

        if (cursor.moveToFirst()) {
            if (cursor.getCount() > 0) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    return true;
                }
            }
        }
        return false;
    }

    public void validateDownloads() {
        List<Long> downloadIds = dbHelper.getDownloadIds();

        for (Long downloadID: downloadIds) {
            if (!isDownloading(downloadID)) {
                dbHelper.deleteDownloadId(downloadID);
            }
        }

        List<String> filenames = dbHelper.getFilenames();

        for (String filename: filenames) {
            if (!FileUtils.fileExists(filename)) {
                dbHelper.deleteStorageFromFilename(filename);
            }
        }
    }

    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        context.registerReceiver(downloadReceiver, intentFilter);
    }

    public void unregisterReceiver() {
        try {
            context.unregisterReceiver(downloadReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void cancelDownload(FeedItem item) {
        Long downloadId = dbHelper.getDownloadId(item.getId());

        if (downloadId != null) {
            downloadManager.remove(downloadId);
            dbHelper.deleteDownloadId(downloadId);
            dbHelper.deleteStorage(item.getId());
        }
    }

    public void completeDownload(long reference) {

        // If download failed/cancelled,
        // the audio file won't exist and the filename in the database should be removed
        if (!isDownloadSuccess(reference)) {
            dbHelper.deleteStorageFromDownloadId(reference);
        }

        dbHelper.deleteDownloadId(reference);
    }
}
