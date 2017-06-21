package me.bromen.podgo.structures;

import android.database.Cursor;

import java.io.Serializable;

/**
 * Created by jeff on 5/31/17.
 */

public class FeedItem implements Serializable {

    private int id;
    private String title;
    private String description;
    private String pubDate;
    private String link;

    private FeedItemEnclosure enclosure;

    public FeedItem() {}

    public FeedItem(String title, String description, String pubDate, String link, FeedItemEnclosure enclosure) {
        this(-1, title, description, pubDate, link, enclosure);
    }

    public FeedItem(int id, String title, String description, String pubDate, String link, FeedItemEnclosure enclosure) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
        this.link = link;
        this.enclosure = enclosure;
    }

    public FeedItem(Cursor cursor) {
        id = cursor.getInt(0);
        title = cursor.getString(1);
        description = cursor.getString(2);
        pubDate = cursor.getString(3);
        link = cursor.getString(4);
        enclosure = new FeedItemEnclosure(cursor.getString(5), cursor.getString(6), cursor.getString(7));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public FeedItemEnclosure getEnclosure() {
        return enclosure;
    }

    public void setEnclosure(FeedItemEnclosure enclosure) {
        this.enclosure = enclosure;
    }
}
