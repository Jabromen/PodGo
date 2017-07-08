package me.bromen.podgo.fragments.mediaplayerbar.mvp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import me.bromen.podgo.R;
import me.bromen.podgo.app.mediaplayer.MediaPlayerService;
import me.bromen.podgo.extras.structures.AudioFile;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.contracts.MediaplayerBarView;

/**
 * Created by Jeffrey on 7/7/2017.
 */

@SuppressLint("ViewConstructor")
public class MediaplayerBarViewImpl extends FrameLayout implements MediaplayerBarView {

    @BindView(R.id.imageview_mediaplayer_bar_episode)
    ImageView episodeImageview;

    @BindView(R.id.textview_mediaplayer_bar_title)
    TextView titleTextview;

    @BindView(R.id.imageview_mediaplayer_bar_playPause)
    ImageView playPauseImageview;

    private final Context context;
    private final Picasso picasso;

    public MediaplayerBarViewImpl(Context context, Picasso picasso) {
        super(context);
        this.context = context;
        this.picasso = picasso;
    }

    public View inflateView() {
        View view = inflate(context, R.layout.fragment_mediaplayer, this);

        ButterKnife.bind(this);

        return view;
    }

    @Override
    public void setAudioFile(AudioFile audioFile) {
        if (audioFile == null) {
            return;
        }

        picasso.load(audioFile.getImageUrl())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .resize(75, 75)
                .centerCrop()
                .into(episodeImageview);

        titleTextview.setText(audioFile.getEpisodeTitle());
    }

    @Override
    public void setState(int state) {
        if (state == MediaPlayerService.PLAYBACK_STOPPED) {
            setVisibility(View.GONE);
        } else if (state == MediaPlayerService.PLAYBACK_PLAYING) {
            setVisibility(View.VISIBLE);
            playPauseImageview.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_media_pause));
        } else if (state == MediaPlayerService.PLAYBACK_PAUSED) {
            setVisibility(View.VISIBLE);
            playPauseImageview.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_media_play));
        }
    }

    @Override
    public Observable<Object> observePlayPauseClick() {
        return RxView.clicks(playPauseImageview);
    }

    @Override
    public Observable<Object> observeBarClick() {
        return RxView.clicks(this);
    }
}
