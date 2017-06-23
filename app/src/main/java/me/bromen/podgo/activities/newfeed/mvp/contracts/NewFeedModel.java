package me.bromen.podgo.activities.newfeed.mvp.contracts;

import me.bromen.podgo.ext.structures.Feed;

/**
 * Created by jeff on 6/21/17.
 */

public interface NewFeedModel {

    String downloadFeed(String url);

    void startItunesSearchActivity();
}
