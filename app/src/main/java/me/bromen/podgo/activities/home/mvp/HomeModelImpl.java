package me.bromen.podgo.activities.home.mvp;

import android.app.Activity;

import me.bromen.podgo.activities.home.mvp.contracts.HomeModel;
import me.bromen.podgo.storage.PodcastDbContract;
import me.bromen.podgo.storage.PodcastDbHelper;
import me.bromen.podgo.structures.FeedList;
import rx.Observable;

/**
 * Created by jeff on 6/20/17.
 */

public class HomeModelImpl implements HomeModel {

    private final Activity activity;
    private final PodcastDbHelper dbHelper;

    public HomeModelImpl(Activity activity, PodcastDbHelper dbHelper) {
        this.activity = activity;
        this.dbHelper = dbHelper;
    }

    @Override
    public Observable<FeedList> loadFeeds() {
        return Observable.just(dbHelper.loadAllFeeds(PodcastDbContract.ORDER_ALPHA_ASC));
    }
}
