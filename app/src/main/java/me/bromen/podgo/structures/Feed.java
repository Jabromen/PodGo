package me.bromen.podgo.structures;

import android.database.Cursor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 5/31/17.
 */

public class Feed implements Serializable {

    private long id;
    private String title;
    private String description;
    private String link;
    private String feedUrl;
    private String imageUrl;

    private List<FeedItem> feedItems = new ArrayList<>();

    public Feed() {}

    public Feed(String title, String feedUrl, String imageUrl, String description, String link, List<FeedItem> feedItems) {
        this.title = title;
        this.feedUrl = feedUrl;
        this.imageUrl = imageUrl;
        this.description = description;
        this.link = link;
        this.feedItems = feedItems;
    }

    public Feed(Cursor cursor) {
        id = cursor.getLong(0);
        title = cursor.getString(1);
        description = cursor.getString(2);
        link = cursor.getString(3);
        feedUrl = cursor.getString(4);
        imageUrl = cursor.getString(5);
    }

    public void setItemPlaces() {

        for (int i = 1; i <= feedItems.size(); i++) {
            feedItems.get(feedItems.size() - i).setFeedPlace(i);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<FeedItem> getFeedItems() {
        return feedItems;
    }

    public void setFeedItems(List<FeedItem> feedItems) {
        this.feedItems = feedItems;
    }
}
