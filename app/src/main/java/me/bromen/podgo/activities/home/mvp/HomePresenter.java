package me.bromen.podgo.activities.home.mvp;

import me.bromen.podgo.activities.home.mvp.contracts.HomeModel;
import me.bromen.podgo.activities.home.mvp.contracts.HomeView;
import me.bromen.podgo.activities.home.mvp.contracts.Presenter;
import me.bromen.podgo.activities.home.mvp.view.HomeViewImpl;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by jeff on 6/20/17.
 */

public class HomePresenter implements Presenter {

    private final HomeView view;
    private final HomeModel model;
    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    public HomePresenter(HomeView view, HomeModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void onCreate() {
        compositeSubscription.add(observeLoadFeeds());
    }

    @Override
    public void onDestroy() {
        compositeSubscription.clear();
    }

    private Subscription observeLoadFeeds() {
        return Observable.just(null)
                .doOnNext(__ -> view.showLoading(true))
                .observeOn(Schedulers.io())
                .switchMap(__ -> model.loadFeeds())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnEach(__ -> view.showLoading(false))
                .doOnNext(feeds -> {
                    if (feeds.isEmpty()) {
                        view.showNoFeeds();
                    } else {
                        view.showFeeds(feeds);
                    }
                })
                .subscribe();
    }
}
