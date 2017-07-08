package me.bromen.podgo.activities.mediacontrol.mvp.contracts;

import io.reactivex.Observable;
import me.bromen.podgo.extras.structures.AudioFile;

/**
 * Created by jeff on 7/8/17.
 */

public interface MediaControlView {

    void showControls(boolean showing);

    void setState(int state);

    void setPosition(int position);

    void setDuration(int duration);

    void setAudioFile(AudioFile audioFile);

    Observable<Object> observePlayPause();

    Observable<Object> observeSkipForward();

    Observable<Object> observeSkipBackward();

    Observable<Integer> observeSeekbar();
}
