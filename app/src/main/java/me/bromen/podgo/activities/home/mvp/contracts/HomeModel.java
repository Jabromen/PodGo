package me.bromen.podgo.activities.home.mvp.contracts;

import me.bromen.podgo.ext.structures.FeedList;

/**
 * Created by jeff on 6/20/17.
 */

public interface HomeModel {

    FeedList loadFeeds() throws Exception;

    void startNewFeedActivity();

    void startOptionsActivity();

    void startFeedDetailActivity(long id);
}
