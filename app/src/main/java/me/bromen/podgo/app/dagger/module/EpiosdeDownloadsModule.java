package me.bromen.podgo.app.dagger.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import me.bromen.podgo.app.dagger.AppScope;
import me.bromen.podgo.app.downloads.EpisodeDownloads;
import me.bromen.podgo.app.storage.DbHelper;

/**
 * Created by jeff on 6/20/17.
 */

@Module
public class EpiosdeDownloadsModule {

    @AppScope
    @Provides
    public EpisodeDownloads episodeDownloads(Context context, DbHelper dbHelper) {
        return new EpisodeDownloads(context, dbHelper);
    }
}
