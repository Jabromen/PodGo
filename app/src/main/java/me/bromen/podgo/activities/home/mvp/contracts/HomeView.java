package me.bromen.podgo.activities.home.mvp.contracts;

import java.util.List;

import me.bromen.podgo.extras.structures.Feed;
import me.bromen.podgo.extras.structures.FeedList;
import io.reactivex.Observable;

/**
 * Created by jeff on 6/20/17.
 */

public interface HomeView {

    void showNoFeeds();

    void showFeeds(List<Feed> feeds);

    void showLoading(boolean loading);

    void showError();

    void showFeedOptions();

    void showNewEpisodes(int newEps);

    Observable<Integer> observeMenuItemClick();

    Observable<Feed> observeFeedTileClick();

    Observable<Feed> observeFeedOptionsClick();

    Observable<Integer> observeFeedOptionMenuClick();
}
