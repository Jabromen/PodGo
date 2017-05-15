package me.bromen.podgo;

import com.icosillion.podengine.exceptions.MalformedFeedException;
import com.icosillion.podengine.models.Podcast;

import java.io.Serializable;
import java.net.MalformedURLException;

/**
 * Shell class that mimics a Podcast object.
 * It holds only the data necessary to build the UI.
 */

public class PodcastShell implements Serializable {

    private String title;
    private int numEpisodes;
    private String feedUrl;
    private String imageUrl;

    public PodcastShell(Podcast podcast) {
        try {
            title = podcast.getTitle();
        } catch (MalformedFeedException e) {
            title = "No Title";
        }

            numEpisodes = podcast.getEpisodes().size();
            feedUrl = podcast.getFeedURL().toString();

        try {
            imageUrl = podcast.getImageURL().toString();
        } catch (MalformedURLException e) {
            imageUrl = "";
        }
    }

    public PodcastShell(String title, int numEpisodes, String feedUrl, String imageUrl) {
        this.title = title;
        this.numEpisodes = numEpisodes;
        this.feedUrl = feedUrl;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public int getNumEpisodes() {
        return numEpisodes;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
