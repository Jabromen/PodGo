package me.bromen.podgo.activities.newfeed.mvp.contracts;

import io.reactivex.Observable;

/**
 * Created by jeff on 6/21/17.
 */

public interface NewFeedView {

    void showDownloadSuccess();

    void showDownloadError(String reason);

    void showSaveSuccess();

    void showSaveError(String reason);

    Observable<Object> observeItunesButton();

    Observable<String> observeManualButton();
}
