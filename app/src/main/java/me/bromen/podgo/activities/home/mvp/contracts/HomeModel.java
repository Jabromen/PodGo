package me.bromen.podgo.activities.home.mvp.contracts;

import me.bromen.podgo.structures.FeedList;
import rx.Observable;

/**
 * Created by jeff on 6/20/17.
 */

public interface HomeModel {

    Observable<FeedList> loadFeeds();
}
