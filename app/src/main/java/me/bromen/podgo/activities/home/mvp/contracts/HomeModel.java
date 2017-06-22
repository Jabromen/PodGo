package me.bromen.podgo.activities.home.mvp.contracts;

import io.reactivex.Observable;
import io.reactivex.Single;
import me.bromen.podgo.structures.FeedList;

/**
 * Created by jeff on 6/20/17.
 */

public interface HomeModel {

    FeedList loadFeeds() throws Exception;

    void startNewFeedActivity();

    void startOptionsActivity();
}
