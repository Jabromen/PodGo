package me.bromen.podgo.activities.mediacontrol.mvp;

import android.util.Log;

import io.reactivex.Observable;
import me.bromen.podgo.activities.mediacontrol.mvp.contracts.MediaControlModel;
import me.bromen.podgo.app.mediaplayer.MediaPlayerServiceController;
import me.bromen.podgo.extras.structures.AudioFile;

/**
 * Created by jeff on 7/8/17.
 */

public class MediaControlModelImpl implements MediaControlModel {

    private final MediaPlayerServiceController controller;

    public MediaControlModelImpl(MediaPlayerServiceController controller) {
        this.controller = controller;
    }

    @Override
    public void playPause() {
        controller.playPause();
    }

    @Override
    public void seekForward() {
        controller.seekForward();
    }

    @Override
    public void seekBackward() {
        controller.seekBackward();
    }

    @Override
    public void seekDirect(int seekTo) {
        controller.seekDirect(seekTo);
    }

    @Override
    public int getInitialState() {
        return controller.getState();
    }

    @Override
    public int getInitialPosition() {
        return controller.getCurrentPosition();
    }

    @Override
    public int getInitialDuration() {
        return controller.getDuration();
    }

    @Override
    public AudioFile getInitialAudioFile() {
        return controller.getCurrentAudio();
    }

    @Override
    public Observable<Integer> observeState() {
        return controller.observeState();
    }

    @Override
    public Observable<Integer> observePosition() {
        return controller.observeCurrentPosition();
    }

    @Override
    public Observable<Integer> observeDuration() {
        return controller.observeDuration();
    }

    @Override
    public Observable<AudioFile> observeAudioFile() {
        return controller.observeAudioFile();
    }
}
