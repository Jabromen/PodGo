package me.bromen.podgo.activities.mediacontrol.mvp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxSeekBar;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;
import me.bromen.podgo.R;
import me.bromen.podgo.activities.mediacontrol.mvp.contracts.MediaControlView;
import me.bromen.podgo.app.mediaplayer.MediaPlayerService;
import me.bromen.podgo.extras.structures.AudioFile;
import me.bromen.podgo.extras.utilities.TimeUtils;

/**
 * Created by jeff on 7/8/17.
 */

@SuppressLint("ViewConstructor")
public class MediaControlViewImpl extends FrameLayout implements MediaControlView {

    @BindView(R.id.textview_mediacontrol_episodetitle)
    TextView episodeTitle;

    @BindView(R.id.textview_mediacontrol_podcasttitle)
    TextView podcastTitle;

    @BindView(R.id.imageview_mediacontrol_episodeimage)
    ImageView imageView;

    @BindView(R.id.textview_mediacontrol_elapsedtime)
    TextView elapsedTime;

    @BindView(R.id.textview_mediacontrol_remainingtime)
    TextView remainingTime;

    @BindView(R.id.seekbar_mediacontrol)
    SeekBar seekBar;

    @BindView(R.id.imageview_mediacontrol_playPause)
    ImageView playPauseButton;

    @BindView(R.id.imageview_mediacontrol_skipBackward)
    ImageView skipBackwardButton;

    @BindView(R.id.imageview_mediacontrol_skipForward)
    ImageView skipForwardButton;

    @BindView(R.id.textview_mediacontrol_hidecontrols)
    TextView hideControls;

    @BindView(R.id.relativelayout_mediacontrol_container)
    RelativeLayout controlsContainer;

    @BindView(R.id.toolbar_mediacontrol)
    Toolbar toolbar;

    private final Picasso picasso;

    private final PublishSubject<Integer> seekbarObservable = PublishSubject.create();

    private boolean userTouching = false;

    public MediaControlViewImpl(Activity activity, Picasso picasso) {
        super(activity);
        this.picasso = picasso;

        inflate(getContext(), R.layout.activity_mediacontrol, this);

        ButterKnife.bind(this);

        observeSeekbarChanges();
    }

    private void observeSeekbarChanges() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateSeekbar(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                userTouching = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                userTouching = false;
                seekbarObservable.onNext(seekBar.getProgress());
            }
        });
    }

    private void updateSeekbar(int position) {
        seekBar.setProgress(position);
        elapsedTime.setText(TimeUtils.msecToHMS(position));
        remainingTime.setText(TimeUtils.msecToHMS(seekBar.getMax() - position));
    }

    @Override
    public void showControls(boolean showing) {
        if (showing) {
            controlsContainer.setVisibility(View.VISIBLE);
            hideControls.setVisibility(View.GONE);
        } else {
            controlsContainer.setVisibility(View.GONE);
            hideControls.setVisibility(View.VISIBLE);
            toolbar.setTitle("Not Playing");
        }
    }

    @Override
    public void setState(int state) {
        if (state == MediaPlayerService.PLAYBACK_PLAYING) {
            playPauseButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_media_pause));
            seekBar.setEnabled(true);
        } else if (state == MediaPlayerService.PLAYBACK_PAUSED) {
            playPauseButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_media_play));
            seekBar.setEnabled(false);
        }
    }

    @Override
    public void setPosition(int position) {
        if (!userTouching) {
            updateSeekbar(position);
        }
    }

    @Override
    public void setDuration(int duration) {
        seekBar.setMax(duration);
    }

    @Override
    public void setAudioFile(AudioFile audioFile) {
        if (audioFile == null) {
            return;
        }
        toolbar.setTitle(audioFile.getEpisodeTitle());
        episodeTitle.setText(audioFile.getEpisodeTitle());
        podcastTitle.setText(audioFile.getPodcastTitle());

        picasso.load(audioFile.getImageUrl())
                .resize(250, 250)
                .centerCrop()
                .into(imageView);
    }

    @Override
    public Observable<Object> observePlayPause() {
        return RxView.clicks(playPauseButton);
    }

    @Override
    public Observable<Object> observeSkipForward() {
        return RxView.clicks(skipForwardButton);
    }

    @Override
    public Observable<Object> observeSkipBackward() {
        return RxView.clicks(skipBackwardButton);
    }

    @Override
    public Observable<Integer> observeSeekbar() {
        return seekbarObservable;
    }
}
