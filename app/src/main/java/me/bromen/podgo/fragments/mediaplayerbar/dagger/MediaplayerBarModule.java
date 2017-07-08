package me.bromen.podgo.fragments.mediaplayerbar.dagger;

import android.app.Activity;

import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import me.bromen.podgo.app.mediaplayer.MediaPlayerServiceController;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.MediaplayerBarModelImpl;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.MediaplayerBarViewImpl;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.contracts.MediaplayerBarModel;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.MediaplayerBarPresenter;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.contracts.MediaplayerBarView;

/**
 * Created by Jeffrey on 7/7/2017.
 */

@Module
public class MediaplayerBarModule {

    private final Activity activity;

    public MediaplayerBarModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    @MediaplayerBarScope
    public MediaplayerBarView view(Picasso picasso) {
        return new MediaplayerBarViewImpl(activity, picasso);
    }

    @Provides
    @MediaplayerBarScope
    public MediaplayerBarModel model(MediaPlayerServiceController controller) {
        return new MediaplayerBarModelImpl(activity, controller);
    }

    @Provides
    @MediaplayerBarScope
    public MediaplayerBarPresenter presenter(MediaplayerBarView view, MediaplayerBarModel model) {
        return new MediaplayerBarPresenter(view, model);
    }
}
