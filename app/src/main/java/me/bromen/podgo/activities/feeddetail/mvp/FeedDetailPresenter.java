package me.bromen.podgo.activities.feeddetail.mvp;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import me.bromen.podgo.activities.Presenter;
import me.bromen.podgo.activities.feeddetail.mvp.contracts.FeedDetailModel;
import me.bromen.podgo.activities.feeddetail.mvp.contracts.FeedDetailView;
import me.bromen.podgo.app.mediaplayer.MediaPlayerService;
import me.bromen.podgo.extras.structures.FeedItem;

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
        view.showMediaplayerBar(model.getInitialMediaState() != MediaPlayerService.PLAYBACK_STOPPED);

        observeDownloads();
        loadFeed(true);
        observeItemTileClick();
        observeItemActionClick();
        observeMediaState();
    }

    @Override
    public void onDestroy() {
        disposables.dispose();
    }

    private void observeDownloads() {
        disposables.add(model.observeDownloads()
                .subscribe(__ -> loadFeed(false)));
    }

    // Load feed info from database
    private void loadFeed(boolean showLoading) {
        if (showLoading) {
            view.showLoading(true);
        }
        disposables.add(Observable.fromCallable(() -> model.loadFeed(feedId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(feed -> {
                    if (showLoading) {
                        view.showLoading(false);
                    }
                    view.showFeed(feed);
                }, throwable -> {
                    if (showLoading) {
                        view.showLoading(false);
                    }
                    view.showError();
                }));
    }

    // Observe UI Events

    private void observeItemTileClick() {
        disposables.add(view.observeItemTileClick()
                .subscribe(model::startFeedItemDetailActivity));
    }

    private void observeItemActionClick() {
        disposables.add(view.observeItemActionClick()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    if (item.isDownloading()) {
                        cancelDownload(item);
                    } else if (item.isDownloaded()) {
                        model.playEpisode(item);
                    } else {
                        startDownload(item);
                    }
                }));
    }

    private void observeMediaState() {
        disposables.add(model.observeMediaState()
                .subscribe(state -> view.showMediaplayerBar(state != MediaPlayerService.PLAYBACK_STOPPED)));
    }

    private void startDownload(FeedItem item) {
        disposables.add(Observable.fromCallable(() -> model.startDownload(item))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> loadFeed(false)));
    }

    private void cancelDownload(FeedItem item) {
        disposables.add(Observable.fromCallable(() -> model.cancelDownload(item))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> loadFeed(false)));
    }
}
