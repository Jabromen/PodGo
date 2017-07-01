package me.bromen.podgo.app.dagger;

import android.content.Context;

import com.squareup.picasso.Picasso;

import dagger.Component;
import me.bromen.podgo.app.dagger.module.MediaPlayerModule;
import me.bromen.podgo.app.mediaplayer.MediaPlayerServiceController;
import me.bromen.podgo.app.parser.FeedParser;
import me.bromen.podgo.app.dagger.module.AppModule;
import me.bromen.podgo.app.dagger.module.DatabaseModule;
import me.bromen.podgo.app.dagger.module.EpiosdeDownloadsModule;
import me.bromen.podgo.app.dagger.module.NetworkModule;
import me.bromen.podgo.app.dagger.module.ParserModule;
import me.bromen.podgo.app.downloads.EpisodeDownloads;
import me.bromen.podgo.app.storage.DbHelper;
import okhttp3.OkHttpClient;

/**
 * Created by jeff on 6/20/17.
 */

@AppScope
@Component(modules = {NetworkModule.class, ParserModule.class, MediaPlayerModule.class,
        DatabaseModule.class, AppModule.class, EpiosdeDownloadsModule.class})
public interface AppComponent {

    Context context();

    OkHttpClient okHttpClient();

    Picasso picasso();

    FeedParser feedParser();

    DbHelper dbHelper();

    EpisodeDownloads episodeDownloads();

    MediaPlayerServiceController mediaPlayerServiceController();
}
