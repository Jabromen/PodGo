package me.bromen.podgo.activities.feeddetail.mvp;

import android.util.Log;

import me.bromen.podgo.activities.feeddetail.FeedDetailActivity;
import me.bromen.podgo.activities.feeddetail.mvp.contracts.FeedDetailModel;
import me.bromen.podgo.app.storage.PodcastDbHelper;
import me.bromen.podgo.downloads.EpisodeDownloads;
import me.bromen.podgo.ext.structures.Feed;
import me.bromen.podgo.ext.structures.FeedItem;

/**
 * Created by jeff on 6/22/17.
 */

public class FeedDetailModelImpl implements FeedDetailModel {

    public static String TAG = "FeedDetailModelImpl";

    private final FeedDetailActivity activity;
    private final PodcastDbHelper dbHelper;
    private final EpisodeDownloads episodeDownloads;

    public FeedDetailModelImpl(FeedDetailActivity activity, PodcastDbHelper dbHelper,
                               EpisodeDownloads episodeDownloads) {
        this.activity = activity;
        this.dbHelper = dbHelper;
        this.episodeDownloads = episodeDownloads;
    }

    @Override
    public Feed loadFeed(long feedId) throws Exception {
        Feed feed = dbHelper.loadFeed(feedId);
        feed.setFeedItems(dbHelper.loadFeedItems(feedId));
        return feed;
    }

    @Override
    public void downloadEpisode(FeedItem item) throws Exception {
        Log.d(TAG, "downloadEpisode(): " + item.getTitle());
    }

    @Override
    public void startFeedItemDetailActivity(FeedItem item) {
        Log.d(TAG, "startFeedItemDetailActivity(): " + item.getTitle());
    }
}
