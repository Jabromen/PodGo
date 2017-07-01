package me.bromen.podgo.app;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import me.bromen.podgo.BuildConfig;
import me.bromen.podgo.app.dagger.AppComponent;
import me.bromen.podgo.app.dagger.DaggerAppComponent;
import me.bromen.podgo.app.dagger.module.AppModule;
import me.bromen.podgo.app.mediaplayer.MediaPlayerService;
import timber.log.Timber;

/**
 * Created by jeff on 6/20/17.
 */

public class PodGoApplication extends Application {

    public static PodGoApplication get(Activity activity) {
        return (PodGoApplication) activity.getApplication();
    }

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        component.episodeDownloads().registerReceiver();
        component.episodeDownloads().validateDownloads();
        component.mediaPlayerServiceController().bind();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        component.episodeDownloads().unregisterReceiver();
        component.mediaPlayerServiceController().stop();
    }

    public AppComponent component() {
        return this.component;
    }
}
