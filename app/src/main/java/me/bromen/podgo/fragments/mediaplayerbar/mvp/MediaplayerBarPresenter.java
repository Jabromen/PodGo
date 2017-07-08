package me.bromen.podgo.fragments.mediaplayerbar.mvp;

import io.reactivex.disposables.CompositeDisposable;
import me.bromen.podgo.activities.Presenter;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.contracts.MediaplayerBarModel;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.contracts.MediaplayerBarView;

/**
 * Created by Jeffrey on 7/7/2017.
 */

public class MediaplayerBarPresenter implements Presenter {

    private final MediaplayerBarView view;
    private final MediaplayerBarModel model;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public MediaplayerBarPresenter(MediaplayerBarView view, MediaplayerBarModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void onCreate() {
        view.setState(model.getInitialState());
        view.setAudioFile(model.getInitialAudioFile());

        observePlayPauseClick();
        observeBarClick();
        observeState();
        observeAudioFile();
    }

    @Override
    public void onDestroy() {
        disposables.dispose();
    }

    private void observePlayPauseClick() {
        disposables.add(view.observePlayPauseClick().subscribe(__ -> model.playPause()));
    }

    private void observeBarClick() {
        disposables.add(view.observeBarClick().subscribe(__ -> model.clickBar()));
    }

    private void observeState() {
        disposables.add(model.observeState().subscribe(view::setState));
    }

    private void observeAudioFile() {
        disposables.add(model.observeAudioFile().subscribe(view::setAudioFile));
    }
}
