package me.bromen.podgo.activities.itunessearch.mvp.contracts;

import java.util.List;

import me.bromen.podgo.ext.structures.ItunesPodcast;

/**
 * Created by jeff on 6/22/17.
 */

public interface ItunesSearchModel {

    List<ItunesPodcast> searchItunes(String query);

    String downloadFeed(String url);
}
