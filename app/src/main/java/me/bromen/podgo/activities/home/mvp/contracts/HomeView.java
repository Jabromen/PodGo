package me.bromen.podgo.activities.home.mvp.contracts;

import java.util.List;

import io.reactivex.Observable;
import me.bromen.podgo.extras.structures.Feed;

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

    void showMediaplayerBar(boolean show);

    Observable<Integer> observeMenuItemClick();

    Observable<Feed> observeFeedTileClick();

    Observable<Feed> observeFeedOptionsClick();

    Observable<Integer> observeFeedOptionMenuClick();
}
