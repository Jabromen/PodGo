package me.bromen.podgo.fragments.mediaplayerbar.mvp.contracts;

import io.reactivex.Observable;
import me.bromen.podgo.extras.structures.AudioFile;

/**
 * Created by Jeffrey on 7/7/2017.
 */

public interface MediaplayerBarModel {

    void playPause();

    void clickBar();

    int getInitialState();

    AudioFile getInitialAudioFile();

    Observable<Integer> observeState();

    Observable<AudioFile> observeAudioFile();
}
