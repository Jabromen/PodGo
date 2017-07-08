package me.bromen.podgo.fragments.mediaplayerbar.mvp;

import android.app.Activity;
import android.util.Log;

import io.reactivex.Observable;
import me.bromen.podgo.app.mediaplayer.MediaPlayerServiceController;
import me.bromen.podgo.extras.structures.AudioFile;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.contracts.MediaplayerBarModel;

/**
 * Created by Jeffrey on 7/7/2017.
 */

public class MediaplayerBarModelImpl implements MediaplayerBarModel {

    private final Activity activity;
    private final MediaPlayerServiceController controller;

    public MediaplayerBarModelImpl(Activity activity, MediaPlayerServiceController controller) {
        this.activity = activity;
        this.controller = controller;
    }

    @Override
    public void playPause() {
        controller.playPause();
        Log.d(this.toString() , "playPauseClicked");
    }

    @Override
    public void clickBar() {
        // Start MediaPlayer Activity
        Log.d(this.toString(), "barClicked");
    }

    @Override
    public int getInitialState() {
        return controller.getState();
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
    public Observable<AudioFile> observeAudioFile() {
        return controller.observeAudioFile();
    }


}
