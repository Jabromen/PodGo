package me.bromen.podgo.activities.itunessearch.mvp;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import me.bromen.podgo.activities.Presenter;
import me.bromen.podgo.activities.itunessearch.mvp.contracts.ItunesSearchModel;
import me.bromen.podgo.activities.itunessearch.mvp.contracts.ItunesSearchView;

/**
 * Created by jeff on 6/22/17.
 */

public class ItunesSearchPresenter implements Presenter {

    private final ItunesSearchView view;
    private final ItunesSearchModel model;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public ItunesSearchPresenter(ItunesSearchView view, ItunesSearchModel model) {
        this.view = view;
        this.model = model;
    }

    // Android activity lifecycle ties

    @Override
    public void onCreate() {
        view.showNoSearchResults();
        observeSearchQuery();
        observeDownloadFeedClick();
    }

    @Override
    public void onDestroy() {
        disposables.dispose();
    }

    // Observe UI events

    private void observeSearchQuery() {
        disposables.add(view.observeSearchQuery()
                .subscribe(this::search));
    }

    private void observeDownloadFeedClick() {
        disposables.add(view.observeDownloadFeedClick().subscribe(this::downloadFeed));
    }

    // Event handling

    private void search(String query) {
        view.showLoadingResults(true);
        disposables.add(Observable.fromCallable(() -> model.searchItunes(query))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(results -> {
                    view.showLoadingResults(false);
                    if (results.size() == 0) {
                        view.showNoSearchResults();
                    } else {
                        view.showSearchResults(results);
                    }
                }));
    }

    private void downloadFeed(String url) {
        view.showLoadingFeed(true);
        disposables.add(Observable.fromCallable(() -> model.downloadFeed(url))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    view.showLoadingFeed(false);
                    if ("".equals(response)) {
                        view.showDownloadSuccess();
                    } else {
                        view.showDownloadError(response);
                    }
                }));
    }
}
