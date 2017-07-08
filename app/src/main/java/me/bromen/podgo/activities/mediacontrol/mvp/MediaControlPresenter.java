package me.bromen.podgo.activities.mediacontrol.mvp;

import io.reactivex.disposables.CompositeDisposable;
import me.bromen.podgo.activities.Presenter;
import me.bromen.podgo.activities.mediacontrol.mvp.contracts.MediaControlModel;
import me.bromen.podgo.activities.mediacontrol.mvp.contracts.MediaControlView;
import me.bromen.podgo.app.mediaplayer.MediaPlayerService;

/**
 * Created by jeff on 7/8/17.
 */

public class MediaControlPresenter implements Presenter {

    private final MediaControlView view;
    private final MediaControlModel model;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public MediaControlPresenter(MediaControlView view, MediaControlModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void onCreate() {
        // Set initial conditions
        setState(model.getInitialState());
        view.setPosition(model.getInitialPosition());
        view.setDuration(model.getInitialDuration());
        view.setAudioFile(model.getInitialAudioFile());

        // Observe changes
        observeState();
        observePosition();
        observeDuration();
        observeAudioFile();
        observePlayPause();
        observeSkipForward();
        observeSkipBackward();
        observeSeekbar();
    }

    @Override
    public void onDestroy() {
        disposables.dispose();
    }

    private void observeState() {
        disposables.add(model.observeState().subscribe(this::setState));
    }

    private void setState(int state) {
        view.showControls(state != MediaPlayerService.PLAYBACK_STOPPED);
        view.setState(state);
    }

    private void observeAudioFile() {
        disposables.add(model.observeAudioFile().subscribe(view::setAudioFile));
    }

    private void observePosition() {
        disposables.add(model.observePosition().subscribe(view::setPosition));
    }

    private void observeDuration() {
        disposables.add(model.observeDuration().subscribe(view::setDuration));
    }

    private void observePlayPause() {
        disposables.add(view.observePlayPause().subscribe(__ -> model.playPause()));
    }

    private void observeSkipForward() {
        disposables.add(view.observeSkipForward().subscribe(__ -> model.seekForward()));
    }

    private void observeSkipBackward() {
        disposables.add(view.observeSkipBackward().subscribe(__ -> model.seekBackward()));
    }

    private void observeSeekbar() {
        disposables.add(view.observeSeekbar().subscribe(model::seekDirect));
    }

}
