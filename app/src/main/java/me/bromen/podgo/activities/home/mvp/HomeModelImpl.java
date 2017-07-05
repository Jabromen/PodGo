package me.bromen.podgo.activities.home.mvp;

import android.util.Log;

import java.util.List;

import me.bromen.podgo.activities.feeddetail.FeedDetailActivity;
import me.bromen.podgo.activities.home.MainActivity;
import me.bromen.podgo.activities.home.mvp.contracts.HomeModel;
import me.bromen.podgo.activities.newfeed.NewFeedActivity;
import me.bromen.podgo.app.parser.FeedParser;
import me.bromen.podgo.app.storage.DbHelper;
import me.bromen.podgo.extras.structures.Feed;

/**
 * Created by jeff on 6/20/17.
 */

public class HomeModelImpl implements HomeModel {

    public static final String TAG = "HomeModel";

    private final MainActivity activity;
    private final DbHelper dbHelper;
    private final FeedParser feedParser;

    public HomeModelImpl(MainActivity activity, DbHelper dbHelper, FeedParser feedParser) {
        this.activity = activity;
        this.dbHelper = dbHelper;
        this.feedParser = feedParser;
    }

    // Load all feeds from database, should be called asynchronously
    @Override
    public List<Feed> loadFeeds() throws Exception {
        return dbHelper.loadAllFeeds(DbHelper.ORDER_ALPHA_ASC);
    }

    // Check for feed updates, should be called asynchonously
    @Override
    public Integer refreshFeed(Feed feed) throws Exception {
        return feedParser.refreshFeed(feed);
    }

    // Deletes a feed from database, should be called asynchronously
    @Override
    public Boolean deleteFeed(Feed feed) throws Exception {
        return dbHelper.deleteFeed(feed.getId());
    }

    // Starts NewFeedActivity
    @Override
    public void startNewFeedActivity() {
        Log.d(TAG, "startNewFeedActivity()");
        NewFeedActivity.start(activity);
    }

    // Starts OptionsActivity TODO: Implement OptionsActivity
    @Override
    public void startOptionsActivity() {
        Log.d(TAG, "startOptionsActivity()");
    }

    // Starts FeedDetailActivity
    @Override
    public void startFeedDetailActivity(long id) {
        Log.d(TAG, "startFeedDetailActivity() - " + id);
        FeedDetailActivity.start(activity, id);
    }
}
