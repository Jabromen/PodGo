package me.bromen.podgo.app.dagger.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import me.bromen.podgo.app.dagger.AppScope;
import me.bromen.podgo.app.storage.PodcastDbHelper;

/**
 * Created by jeff on 6/20/17.
 */

@Module
public class DatabaseModule {

    @AppScope
    @Provides
    public PodcastDbHelper dbHelper(Context context) {
        return new PodcastDbHelper(context);
    }
}
