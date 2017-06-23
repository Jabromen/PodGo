package me.bromen.podgo.activities.newfeed.mvp;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import me.bromen.podgo.activities.Presenter;
import me.bromen.podgo.activities.newfeed.mvp.contracts.NewFeedModel;
import me.bromen.podgo.activities.newfeed.mvp.contracts.NewFeedView;

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

    // Android activity lifecycle ties

    @Override
    public void onCreate() {
        observeItunesButton();
        observeManualButton();
    }

    @Override
    public void onDestroy() {
        disposables.dispose();
    }

    // Observe UI events

    private void observeItunesButton() {
        disposables.add(view.observeItunesButton()
                .subscribe(__ -> model.startItunesSearchActivity()));
    }

    private void observeManualButton() {
        disposables.add(view.observeManualButton()
                .subscribe(this::onManualButtonClicked));
    }

    // Event handling

    private void onManualButtonClicked(String url) {
        view.showLoading(true);
        disposables.add(Observable.fromCallable(() -> model.downloadFeed(url))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    view.showLoading(false);
                    if ("".equals(response)) {
                        view.showDownloadSuccess();
                    } else {
                        view.showError(response);
                    }
                }));
    }
}
