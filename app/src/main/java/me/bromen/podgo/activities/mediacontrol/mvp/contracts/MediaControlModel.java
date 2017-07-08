package me.bromen.podgo.activities.mediacontrol.mvp.contracts;

import io.reactivex.Observable;
import me.bromen.podgo.extras.structures.AudioFile;

/**
 * Created by jeff on 7/8/17.
 */

public interface MediaControlModel {

    void playPause();

    void seekForward();

    void seekBackward();

    void seekDirect(int seekTo);

    int getInitialState();

    int getInitialPosition();

    int getInitialDuration();

    AudioFile getInitialAudioFile();

    Observable<Integer> observeState();

    Observable<Integer> observePosition();

    Observable<Integer> observeDuration();

    Observable<AudioFile> observeAudioFile();

}
