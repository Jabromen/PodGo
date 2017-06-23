package me.bromen.podgo.activities.newfeed.mvp.contracts;

import io.reactivex.Observable;

/**
 * Created by jeff on 6/21/17.
 */

public interface NewFeedView {

    void showDownloadSuccess();

    void showError(String reason);

    void showLoading(boolean loading);

    Observable<Object> observeItunesButton();

    Observable<String> observeManualButton();
}
