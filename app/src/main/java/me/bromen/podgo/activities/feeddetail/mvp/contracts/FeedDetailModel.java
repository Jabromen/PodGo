package me.bromen.podgo.activities.feeddetail.mvp.contracts;

import io.reactivex.Observable;
import me.bromen.podgo.extras.structures.Feed;
import me.bromen.podgo.extras.structures.FeedItem;

/**
 * Created by jeff on 6/22/17.
 */

public interface FeedDetailModel {

    Feed loadFeed(long feedId) throws Exception;

    Observable<Boolean> observeDownloads();

    Boolean startDownload(FeedItem item);

    Boolean cancelDownload(FeedItem item);

    int getInitialMediaState();

    Observable<Integer> observeMediaState();

    void playEpisode(FeedItem item);

    void startFeedItemDetailActivity(FeedItem item);
}
