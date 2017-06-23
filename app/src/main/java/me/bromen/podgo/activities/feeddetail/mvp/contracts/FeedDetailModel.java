package me.bromen.podgo.activities.feeddetail.mvp.contracts;

import me.bromen.podgo.extras.structures.Feed;
import me.bromen.podgo.extras.structures.FeedItem;

/**
 * Created by jeff on 6/22/17.
 */

public interface FeedDetailModel {

    Feed loadFeed(long feedId) throws Exception;

    void downloadEpisode(FeedItem item) throws Exception;

    void startFeedItemDetailActivity(FeedItem item);
}
