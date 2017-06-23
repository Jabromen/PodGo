package me.bromen.podgo.activities.home.mvp.contracts;

import me.bromen.podgo.extras.structures.Feed;
import me.bromen.podgo.extras.structures.FeedList;

/**
 * Created by jeff on 6/20/17.
 */

public interface HomeModel {

    FeedList loadFeeds() throws Exception;

    Integer refreshFeed(Feed feed) throws Exception;

    Boolean deleteFeed(Feed feed) throws Exception;

    void startNewFeedActivity();

    void startOptionsActivity();

    void startFeedDetailActivity(long id);
}
