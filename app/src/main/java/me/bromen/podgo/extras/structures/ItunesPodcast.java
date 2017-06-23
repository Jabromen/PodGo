package me.bromen.podgo.extras.structures;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jeff on 6/1/17.
 */

public class ItunesPodcast implements Serializable {

    private String title;
    private String feedUrl;
    private String imageUrl;

    public ItunesPodcast() {}

    public ItunesPodcast(JSONObject jsonPodcast) throws JSONException {
        title = jsonPodcast.getString("collectionName");
        feedUrl = jsonPodcast.getString("feedUrl");
        imageUrl = jsonPodcast.getString("artworkUrl100");
    }

    public String getTitle() {
        return title;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
