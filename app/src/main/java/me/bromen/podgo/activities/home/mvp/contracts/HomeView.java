package me.bromen.podgo.activities.home.mvp.contracts;

import me.bromen.podgo.structures.FeedList;
import io.reactivex.Observable;

/**
 * Created by jeff on 6/20/17.
 */

public interface HomeView {

    void showNoFeeds();

    void showFeeds(FeedList feeds);

    void showLoading(boolean loading);

    void showError();

    Observable<Integer> observeMenuItemClick();
}
