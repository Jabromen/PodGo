package me.bromen.podgo.activities.newfeed.mvp;

import android.util.Log;

import me.bromen.podgo.app.parser.FeedParser;
import me.bromen.podgo.activities.newfeed.NewFeedActivity;
import me.bromen.podgo.activities.newfeed.mvp.contracts.NewFeedModel;
import me.bromen.podgo.app.storage.PodcastDbHelper;
import me.bromen.podgo.ext.structures.Feed;

/**
 * Created by jeff on 6/21/17.
 */

public class NewFeedModelImpl implements NewFeedModel {

    public static String TAG = "NewFeedModelImpl";

    private final NewFeedActivity activity;
    private final PodcastDbHelper dbHelper;
    private final FeedParser feedParser;

    public NewFeedModelImpl(NewFeedActivity activity, PodcastDbHelper dbHelper, FeedParser feedParser) {
        this.activity = activity;
        this.dbHelper = dbHelper;
        this.feedParser = feedParser;
    }

    @Override
    public Feed downloadFeed(String url) throws Exception {
        return feedParser.parseFeedFromUrl(url);
    }

    @Override
    public Boolean saveFeed(Feed feed) throws Exception {
        return dbHelper.saveFeed(feed);
    }

    @Override
    public void startItunesSearchActivity() {
        Log.d(TAG, "startItunesSearchActivity()");
    }
}
