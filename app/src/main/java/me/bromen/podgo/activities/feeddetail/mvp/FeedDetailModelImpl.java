package me.bromen.podgo.activities.feeddetail.mvp;

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
        return null;
    }

    @Override
    public void downloadEpisode(FeedItem item) throws Exception {

    }

    @Override
    public void startFeedItemDetailActivity(FeedItem item) {

    }
}
