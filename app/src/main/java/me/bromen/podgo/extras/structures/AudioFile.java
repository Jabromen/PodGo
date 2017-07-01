package me.bromen.podgo.extras.structures;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.Serializable;

import me.bromen.podgo.extras.utilities.PodcastFileUtils;

/**
 * Created by jeff on 6/30/17.
 */

public class AudioFile implements Serializable {

    private String podcastTitle;
    private String episodeTitle;
    private String audioSource;
    private String imageUrl;

    public AudioFile(FeedItem item) {
        this.podcastTitle = item.getFeedTitle();
        this.episodeTitle = item.getTitle();
        if (item.isDownloaded() && !item.isDownloading()) {
            this.audioSource = item.getFilename();
        } else {
            this.audioSource = item.getEnclosure().getUrl();
        }
        this.imageUrl = item.getImageUrl();
    }

    public String getPodcastTitle() {
        return podcastTitle;
    }

    public void setPodcastTitle(String podcastTitle) {
        this.podcastTitle = podcastTitle;
    }

    public String getEpisodeTitle() {
        return episodeTitle;
    }

    public void setEpisodeTitle(String episodeTitle) {
        this.episodeTitle = episodeTitle;
    }

    public String getAudioSource() {
        return audioSource;
    }

    public void setAudioSource(String audioSource) {
        this.audioSource = audioSource;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
