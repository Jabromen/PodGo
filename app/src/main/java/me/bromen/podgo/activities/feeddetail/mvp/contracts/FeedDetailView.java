package me.bromen.podgo.activities.feeddetail.mvp.contracts;

import io.reactivex.Observable;
import me.bromen.podgo.ext.structures.Feed;
import me.bromen.podgo.ext.structures.FeedItem;

/**
 * Created by jeff on 6/22/17.
 */

public interface FeedDetailView {

    void showFeed(Feed feed);

    void showError();

    void showLoading(boolean loading);

    Observable<FeedItem> observeItemTileClick();

    Observable<FeedItem> observeItemDownloadClick();
}
