package me.bromen.podgo.extras.structures;

import android.database.Cursor;
import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by jeff on 5/31/17.
 */

public class FeedItem implements Serializable {

    private long id;
    private String title;
    private String description;
    private String pubDate;
    private String link;
    private String imageUrl;

    private FeedItemEnclosure enclosure;

    private boolean isDownloading;
    private boolean isDownloaded;

    private String filename;
    private long downloadId;

    // Fields used for media player
    private String feedTitle;

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
        id = cursor.getLong(0);
        title = cursor.getString(1);
        description = cursor.getString(2);
        pubDate = cursor.getString(3);
        link = cursor.getString(4);
        enclosure = new FeedItemEnclosure(cursor.getString(5), cursor.getString(6), cursor.getString(7));
        filename = cursor.getString(8);
        downloadId = cursor.getLong(9);
        imageUrl = cursor.getString(10);

        isDownloading = downloadId != -1;
        isDownloaded = !"NULL".equals(filename);
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

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean isDownloading) {
        this.isDownloading = isDownloading;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFeedTitle() {
        return feedTitle;
    }

    public void setFeedTitle(String feedTitle) {
        this.feedTitle = feedTitle;
    }
}
