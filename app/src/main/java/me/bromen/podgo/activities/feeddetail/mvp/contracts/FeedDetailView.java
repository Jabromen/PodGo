package me.bromen.podgo.activities.feeddetail.mvp.contracts;

import io.reactivex.Observable;
import me.bromen.podgo.extras.structures.Feed;
import me.bromen.podgo.extras.structures.FeedItem;

/**
 * Created by jeff on 6/22/17.
 */

public interface FeedDetailView {

    void showFeed(Feed feed);

    void showError();

    void showLoading(boolean loading);

    void showMediaplayerBar(boolean show);

    Observable<FeedItem> observeItemTileClick();

    Observable<FeedItem> observeItemActionClick();
}
