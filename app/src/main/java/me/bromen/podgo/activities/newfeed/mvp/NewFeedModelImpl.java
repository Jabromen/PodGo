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
    private final FeedParser feedParser;

    public NewFeedModelImpl(NewFeedActivity activity, FeedParser feedParser) {
        this.activity = activity;
        this.feedParser = feedParser;
    }

    @Override
    public String downloadFeed(String url) {
        return feedParser.parseFeedFromUrl(url);
    }

    @Override
    public void startItunesSearchActivity() {
        Log.d(TAG, "startItunesSearchActivity()");
    }
}
