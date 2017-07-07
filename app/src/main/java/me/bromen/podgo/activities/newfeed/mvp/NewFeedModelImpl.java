package me.bromen.podgo.activities.newfeed.mvp;

import android.util.Log;

import me.bromen.podgo.activities.itunessearch.ItunesSearchActivity;
import me.bromen.podgo.activities.newfeed.NewFeedActivity;
import me.bromen.podgo.activities.newfeed.mvp.contracts.NewFeedModel;
import me.bromen.podgo.app.parser.FeedParser;

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

    // Downloads a feed from a url, should be called asynchronously
    // Returns an empty string on success ("")
    // Returns a string containing the failure reason on error ("reason")
    @Override
    public String downloadFeed(String url) {
        return feedParser.parseFeedFromUrl(url);
    }

    // Starts ItunesSearchActivity
    @Override
    public void startItunesSearchActivity() {
        Log.d(TAG, "startItunesSearchActivity()");
        ItunesSearchActivity.start(activity);
    }
}
