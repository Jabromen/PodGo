package me.bromen.podgo.app.dagger.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import me.bromen.podgo.app.dagger.AppScope;
import me.bromen.podgo.app.mediaplayer.MediaPlayerServiceController;

/**
 * Created by jeff on 6/30/17.
 */

@Module
public class MediaPlayerModule {

    @AppScope
    @Provides
    public MediaPlayerServiceController mediaPlayerServiceController(Context context) {
        return new MediaPlayerServiceController(context);
    }
}
