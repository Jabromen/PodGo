package me.bromen.podgo.app;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import me.bromen.podgo.BuildConfig;
import me.bromen.podgo.app.dagger.AppComponent;
import me.bromen.podgo.app.dagger.DaggerAppComponent;
import me.bromen.podgo.app.dagger.module.AppModule;
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
        Log.d("APP", "register");
        component.episodeDownloads().validateDownloads();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        component.episodeDownloads().unregisterReceiver();
        Log.d("APP", "unregister");
    }

    public AppComponent component() {
        return this.component;
    }
}
