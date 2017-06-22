package me.bromen.podgo.activities.feeddetail.mvp;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
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

    @Override
    public void onCreate() {
        disposables.add(loadFeed());
        disposables.add(onObserveItemTileClick());
        disposables.add(onObserveItemDownloadClick());
    }

    @Override
    public void onDestroy() {
        disposables.dispose();;
    }

    private Disposable loadFeed() {
        view.showLoading(true);
        return Observable.fromCallable(() -> model.loadFeed(feedId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(feed -> {
                    view.showLoading(false);
                    view.showFeed(feed);
                }, throwable -> {
                    view.showLoading(false);
                    view.showError();
                });
    }

    private Disposable onObserveItemTileClick() {
        return view.observeItemTileClick()
                .subscribe(model::startFeedItemDetailActivity);
    }

    private Disposable onObserveItemDownloadClick() {
        return view.observeItemDownloadClick()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model::downloadEpisode);
    }
}
