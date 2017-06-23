package me.bromen.podgo.activities.feeddetail.mvp;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import me.bromen.podgo.activities.Presenter;
import me.bromen.podgo.activities.feeddetail.mvp.contracts.FeedDetailModel;
import me.bromen.podgo.activities.feeddetail.mvp.contracts.FeedDetailView;

/**
 * Created by jeff on 6/22/17.
 */

public class FeedDetailPresenter implements Presenter {

    private final FeedDetailView view;
    private final FeedDetailModel model;
    private final long feedId;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public FeedDetailPresenter(FeedDetailView view, FeedDetailModel model, long feedId) {
        this.view = view;
        this.model = model;
        this.feedId = feedId;
    }

    // Android activity lifecycle ties

    @Override
    public void onCreate() {
        loadFeed();
        observeItemTileClick();
        observeItemDownloadClick();
    }

    @Override
    public void onDestroy() {
        disposables.dispose();;
    }

    // Load feed info from database
    private void loadFeed() {
        view.showLoading(true);
        disposables.add(Observable.fromCallable(() -> model.loadFeed(feedId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(feed -> {
                    view.showLoading(false);
                    view.showFeed(feed);
                }, throwable -> {
                    view.showLoading(false);
                    view.showError();
                }));
    }

    // Observe UI Events

    private void observeItemTileClick() {
        disposables.add(view.observeItemTileClick()
                .subscribe(model::startFeedItemDetailActivity));
    }

    private void observeItemDownloadClick() {
        disposables.add(view.observeItemDownloadClick()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model::downloadEpisode));
    }
}
