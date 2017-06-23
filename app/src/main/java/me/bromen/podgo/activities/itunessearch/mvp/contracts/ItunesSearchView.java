package me.bromen.podgo.activities.itunessearch.mvp.contracts;

import java.util.List;

import io.reactivex.Observable;
import me.bromen.podgo.ext.structures.ItunesPodcast;

/**
 * Created by jeff on 6/22/17.
 */

public interface ItunesSearchView {

    void showSearchResults(List<ItunesPodcast> results);

    void showNoSearchResults();

    void showLoadingResults(boolean loading);

    void showLoadingFeed(boolean loading);

    void showDownloadSuccess();

    void showDownloadError(String reason);

    Observable<String> observeSearchQuery();

    Observable<String> observeDownloadFeedClick();
}
