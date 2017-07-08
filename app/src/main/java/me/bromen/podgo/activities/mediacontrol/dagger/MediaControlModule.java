package me.bromen.podgo.activities.mediacontrol.dagger;

import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import me.bromen.podgo.activities.mediacontrol.MediaControlActivity;
import me.bromen.podgo.activities.mediacontrol.mvp.MediaControlModelImpl;
import me.bromen.podgo.activities.mediacontrol.mvp.MediaControlPresenter;
import me.bromen.podgo.activities.mediacontrol.mvp.MediaControlViewImpl;
import me.bromen.podgo.activities.mediacontrol.mvp.contracts.MediaControlModel;
import me.bromen.podgo.activities.mediacontrol.mvp.contracts.MediaControlView;
import me.bromen.podgo.app.mediaplayer.MediaPlayerServiceController;

/**
 * Created by jeff on 7/8/17.
 */

@Module
public class MediaControlModule {

    private final MediaControlActivity activity;

    public MediaControlModule(MediaControlActivity activity) {
        this.activity = activity;
    }

    @MediaControlScope
    @Provides
    public MediaControlModel mediaControlModel(MediaPlayerServiceController controller) {
        return new MediaControlModelImpl(controller);
    }

    @MediaControlScope
    @Provides
    public MediaControlView mediaControlView(Picasso picasso) {
        return new MediaControlViewImpl(activity, picasso);
    }

    @MediaControlScope
    @Provides
    public MediaControlPresenter mediaControlPresenter(MediaControlView view, MediaControlModel model) {
        return new MediaControlPresenter(view, model);
    }
}
