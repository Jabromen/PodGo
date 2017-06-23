package me.bromen.podgo.activities.newfeed.mvp.contracts;

/**
 * Created by jeff on 6/21/17.
 */

public interface NewFeedModel {

    String downloadFeed(String url);

    void startItunesSearchActivity();
}
