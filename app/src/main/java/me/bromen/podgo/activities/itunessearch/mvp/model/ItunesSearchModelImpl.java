package me.bromen.podgo.activities.itunessearch.mvp.model;

import java.util.List;

import me.bromen.podgo.activities.itunessearch.mvp.contracts.ItunesSearchModel;
import me.bromen.podgo.app.parser.FeedParser;
import me.bromen.podgo.extras.structures.ItunesPodcast;

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

    // Queries Itunes for podcasts matching query, should be called asynchronously
    @Override
    public List<ItunesPodcast> searchItunes(String query) {
        return itunesApiService.query(query);
    }

    // Downloads a feed from a url, should be called asynchronously
    // Returns an empty string on success ("")
    // Returns a string containing the failure reason on error ("reason")
    @Override
    public String downloadFeed(String url) {
        return feedParser.parseFeedFromUrl(url);
    }
}
