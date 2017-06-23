package me.bromen.podgo.activities.feeddetail.mvp;

import android.util.Log;

import me.bromen.podgo.activities.feeddetail.FeedDetailActivity;
import me.bromen.podgo.activities.feeddetail.mvp.contracts.FeedDetailModel;
import me.bromen.podgo.app.storage.PodcastDbHelper;
import me.bromen.podgo.app.downloads.EpisodeDownloads;
import me.bromen.podgo.extras.structures.Feed;
import me.bromen.podgo.extras.structures.FeedItem;

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

    // Loads a feed and its items from database, should be called asynchronously
    // TODO: update FeedItem isDownloaded and isDownloading fields
    @Override
    public Feed loadFeed(long feedId) throws Exception {
        Feed feed = dbHelper.loadFeed(feedId);
        feed.setFeedItems(dbHelper.loadFeedItems(feedId));
        return feed;
    }

    // Starts a download for the episode audio file
    // TODO: Implement Download and update FeedItem isDownloaded and isDownloading fields
    @Override
    public void downloadEpisode(FeedItem item) throws Exception {
        Log.d(TAG, "downloadEpisode(): " + item.getTitle());
    }

    // Starts FeedItemDetailActivity
    // TODO: Implement FeedItemDetailActivity
    @Override
    public void startFeedItemDetailActivity(FeedItem item) {
        Log.d(TAG, "startFeedItemDetailActivity(): " + item.getTitle());
    }
}
