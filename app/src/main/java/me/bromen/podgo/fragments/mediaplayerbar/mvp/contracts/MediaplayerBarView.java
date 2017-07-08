package me.bromen.podgo.fragments.mediaplayerbar.mvp.contracts;

import io.reactivex.Observable;
import me.bromen.podgo.extras.structures.AudioFile;

/**
 * Created by Jeffrey on 7/7/2017.
 */

public interface MediaplayerBarView {

    void setAudioFile(AudioFile audioFile);

    void setState(int state);

    Observable<Object> observePlayPauseClick();

    Observable<Object> observeBarClick();
}
