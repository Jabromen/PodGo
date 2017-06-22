package me.bromen.podgo.activities.newfeed.mvp;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.bromen.podgo.activities.Presenter;
import me.bromen.podgo.activities.newfeed.mvp.contracts.NewFeedModel;
import me.bromen.podgo.activities.newfeed.mvp.contracts.NewFeedView;
import me.bromen.podgo.structures.Feed;

/**
 * Created by jeff on 6/21/17.
 */

public class NewFeedPresenter implements Presenter {

    private final NewFeedView view;
    private final NewFeedModel model;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public NewFeedPresenter(NewFeedView view, NewFeedModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void onCreate() {
        disposables.add(onObserveItunesButton());
        disposables.add(onObserveManualButton());
    }

    @Override
    public void onDestroy() {
        disposables.dispose();
    }

    private Disposable onObserveItunesButton() {
        return view.observeItunesButton()
                .subscribe(__ -> model.startItunesSearchActivity());
    }

    private Disposable onObserveManualButton() {
        return view.observeManualButton()
                .subscribe(this::onManualButtonClicked);
    }

    private void onManualButtonClicked(String url) {
        disposables.add(Observable.fromCallable(() -> model.downloadFeed(url))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(__ -> view.showDownloadSuccess())
                .doOnError(throwable -> view.showDownloadError(throwable.getMessage()))
                .subscribe(this::onFeedDownloaded, throwable -> {}));
    }

    private void onFeedDownloaded(Feed feed) {
        disposables.add(Observable.fromCallable(() -> model.saveFeed(feed))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(saved -> {
                    if (saved) {
                        view.showSaveSuccess();
                    } else {
                        view.showSaveError("Already Saved");
                    }
                }, throwable -> view.showSaveError(throwable.getMessage())));
    }
}
