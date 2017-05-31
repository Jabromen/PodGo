package me.bromen.podgo.structures;

import java.io.Serializable;

/**
 * Created by jeff on 5/31/17.
 */

public class FeedItemEnclosure implements Serializable {

    private String url;
    private String type;
    private String length;

    public FeedItemEnclosure() {}

    public FeedItemEnclosure(String url, String type, String length) {
        this.url = url;
        this.type = type;
        this.length = length;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public String getLength() {
        return length;
    }
}
