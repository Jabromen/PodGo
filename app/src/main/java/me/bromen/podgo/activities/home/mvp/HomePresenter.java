package me.bromen.podgo.activities.home.mvp;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.bromen.podgo.R;
import me.bromen.podgo.activities.home.mvp.contracts.HomeModel;
import me.bromen.podgo.activities.home.mvp.contracts.HomeView;
import me.bromen.podgo.activities.Presenter;
import me.bromen.podgo.activities.home.mvp.view.HomeViewImpl;
import me.bromen.podgo.ext.structures.Feed;

/**
 * Created by jeff on 6/20/17.
 */

public class HomePresenter implements Presenter {

    private final HomeView view;
    private final HomeModel model;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public HomePresenter(HomeView view, HomeModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void onCreate() {
        loadFeeds();
        observeMenuItems();
        observeFeedTile();
        observeFeedOptions();
    }

    @Override
    public void onDestroy() {
        disposables.dispose();
    }

    private void loadFeeds() {

        view.showLoading(true);
        disposables.add(Single.fromCallable(model::loadFeeds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(feeds -> {
                    view.showLoading(false);
                    if (feeds.isEmpty()) {
                        view.showNoFeeds();
                    } else {
                        view.showFeeds(feeds);
                    }
                }, throwable -> {
                    view.showLoading(false);
                    view.showError();
                }));
    }

    private void observeMenuItems() {
        disposables.add(view.observeMenuItemClick()
                .subscribe(itemId -> {
                    if (itemId == R.id.action_new_podcast) {
                        model.startNewFeedActivity();
                    } else if (itemId == R.id.action_settings) {
                        model.startOptionsActivity();
                    }
                }));
    }

    private void observeFeedTile() {
        disposables.add(view.observeFeedTileClick()
                .subscribe(feed -> model.startFeedDetailActivity(feed.getId())));
    }

    private void observeFeedOptions() {
        disposables.add(view.observeFeedOptionsClick()
                .subscribe(this::onFeedOptionsClicked));
    }

    private void onFeedOptionsClicked(Feed feed) {
        view.showFeedOptions();
        disposables.add(view.observeFeedOptionMenuClick()
                .subscribe(i -> {
                    if (i == R.id.action_refresh_feed) {
                        onRefreshFeed(feed);
                    } else if (i == R.id.action_delete_feed) {
                        onDeleteFeed(feed);
                    }
                }));
    }

    private void onRefreshFeed(Feed feed) {
        disposables.add(Single.fromCallable(() -> model.refreshFeed(feed))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::showNewEpisodes, throwable -> view.showError()));
    }

    private void onDeleteFeed(Feed feed) {
        disposables.add(Single.fromCallable(() -> model.deleteFeed(feed))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> loadFeeds(), throwable -> view.showError()));
    }
}
