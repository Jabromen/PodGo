package me.bromen.podgo.activities.home.mvp.contracts;

import java.util.List;

import io.reactivex.Observable;
import me.bromen.podgo.extras.structures.Feed;

/**
 * Created by jeff on 6/20/17.
 */

public interface HomeModel {

    List<Feed> loadFeeds() throws Exception;

    Integer refreshFeed(Feed feed) throws Exception;

    Boolean deleteFeed(Feed feed) throws Exception;

    int getInitialMediaState();

    Observable<Integer> observeMediaState();

    void startNewFeedActivity();

    void startOptionsActivity();

    void startFeedDetailActivity(long id);
}
