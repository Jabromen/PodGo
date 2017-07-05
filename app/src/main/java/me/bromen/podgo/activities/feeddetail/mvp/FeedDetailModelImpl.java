package me.bromen.podgo.activities.feeddetail.mvp;

import android.util.Log;

import io.reactivex.Observable;
import me.bromen.podgo.activities.feeddetail.FeedDetailActivity;
import me.bromen.podgo.activities.feeddetail.mvp.contracts.FeedDetailModel;
import me.bromen.podgo.app.mediaplayer.MediaPlayerServiceController;
import me.bromen.podgo.app.storage.DbHelper;
import me.bromen.podgo.app.downloads.EpisodeDownloads;
import me.bromen.podgo.extras.structures.AudioFile;
import me.bromen.podgo.extras.structures.Feed;
import me.bromen.podgo.extras.structures.FeedItem;

/**
 * Created by jeff on 6/22/17.
 */

public class FeedDetailModelImpl implements FeedDetailModel {

    public static String TAG = "FeedDetailModelImpl";

    private final FeedDetailActivity activity;
    private final DbHelper dbHelper;
    private final EpisodeDownloads episodeDownloads;
    private final MediaPlayerServiceController controller;

    public FeedDetailModelImpl(FeedDetailActivity activity, DbHelper dbHelper,
                               EpisodeDownloads episodeDownloads, MediaPlayerServiceController controller) {
        this.activity = activity;
        this.dbHelper = dbHelper;
        this.episodeDownloads = episodeDownloads;
        this.controller = controller;
    }

    // Loads a feed and its items from database, should be called asynchronously
    @Override
    public Feed loadFeed(long feedId) throws Exception {
        Feed feed = dbHelper.loadFeed(feedId);
        feed.setFeedItems(dbHelper.loadFeedItems(feedId));
        return feed;
    }

    @Override
    public Observable<Boolean> observeDownloads() {
        return episodeDownloads.getDownloadObservable();
    }

    // Starts a download for the episode audio file
    @Override
    public Boolean startDownload(FeedItem item) {
        Log.d(TAG, "startDownload(): " + item.getTitle());
        episodeDownloads.startDownload(item);
        return true;
    }

    @Override
    public Boolean cancelDownload(FeedItem item) {
        episodeDownloads.cancelDownload(item);
        return true;
    }

    @Override
    public void playEpisode(FeedItem item) {
        Log.d(TAG, "playEpisode(): " + item.getTitle());
        controller.play(new AudioFile(item));
    }

    // Starts FeedItemDetailActivity
    // TODO: Implement FeedItemDetailActivity
    @Override
    public void startFeedItemDetailActivity(FeedItem item) {
        Log.d(TAG, "startFeedItemDetailActivity(): " + item.getTitle() + " " + item.getPubDate());
    }
}
