package me.bromen.podgo.activities.home.mvp;

import android.util.Log;

import me.bromen.podgo.activities.feeddetail.FeedDetailActivity;
import me.bromen.podgo.activities.home.MainActivity;
import me.bromen.podgo.activities.home.mvp.contracts.HomeModel;
import me.bromen.podgo.activities.newfeed.NewFeedActivity;
import me.bromen.podgo.app.storage.PodcastDbContract;
import me.bromen.podgo.app.storage.PodcastDbHelper;
import me.bromen.podgo.ext.structures.FeedList;

/**
 * Created by jeff on 6/20/17.
 */

public class HomeModelImpl implements HomeModel {

    public static final String TAG = "HomeModel";

    private final MainActivity activity;
    private final PodcastDbHelper dbHelper;

    public HomeModelImpl(MainActivity activity, PodcastDbHelper dbHelper) {
        this.activity = activity;
        this.dbHelper = dbHelper;
    }

    @Override
    public FeedList loadFeeds() throws Exception {
        return dbHelper.loadAllFeeds(PodcastDbContract.ORDER_ALPHA_ASC);
    }

    @Override
    public void startNewFeedActivity() {
        Log.d(TAG, "startNewFeedActivity()");
        NewFeedActivity.start(activity);
    }

    @Override
    public void startOptionsActivity() {
        Log.d(TAG, "startOptionsActivity()");
    }

    @Override
    public void startFeedDetailActivity(long id) {
        Log.d(TAG, "startFeedDetailActivity() - " + id);
        FeedDetailActivity.start(activity, id);
    }
}
