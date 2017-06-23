package me.bromen.podgo.activities.itunessearch.mvp.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.bromen.podgo.activities.itunessearch.mvp.contracts.ItunesSearchModel;
import me.bromen.podgo.activities.itunessearch.mvp.model.ItunesApiService;
import me.bromen.podgo.app.parser.FeedParser;
import me.bromen.podgo.ext.structures.ItunesPodcast;

/**
 * Created by jeff on 6/22/17.
 */

public class ItunesSearchModelImpl implements ItunesSearchModel {

    public static String TAG = "ItunesSearchModelImpl";

    private final FeedParser feedParser;
    private final ItunesApiService itunesApiService;

    public ItunesSearchModelImpl(FeedParser feedParser, ItunesApiService itunesApiService) {
        this.feedParser = feedParser;
        this.itunesApiService = itunesApiService;
    }

    @Override
    public List<ItunesPodcast> searchItunes(String query) {
        Log.d(TAG, query);
        return itunesApiService.query(query);
    }

    @Override
    public String downloadFeed(String url) {
        return feedParser.parseFeedFromUrl(url);
    }
}
