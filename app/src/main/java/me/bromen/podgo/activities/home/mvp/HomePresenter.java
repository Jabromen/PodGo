package me.bromen.podgo.activities.home.mvp;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.bromen.podgo.R;
import me.bromen.podgo.activities.home.mvp.contracts.HomeModel;
import me.bromen.podgo.activities.home.mvp.contracts.HomeView;
import me.bromen.podgo.activities.Presenter;

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
        disposables.add(loadFeeds());
        disposables.add(observeMenuItems());
    }

    @Override
    public void onDestroy() {
        disposables.dispose();
    }

    private Disposable loadFeeds() {

        view.showLoading(true);
        return Single.fromCallable(model::loadFeeds)
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
                });
    }

    private Disposable observeMenuItems() {
        return view.observeMenuItemClick()
                .subscribe(itemId -> {
                    if (itemId == R.id.action_new_podcast) {
                        model.startNewFeedActivity();
                    } else if (itemId == R.id.action_settings) {
                        model.startOptionsActivity();
                    }
                });
    }
}
