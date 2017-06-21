package me.bromen.podgo.app.dagger.module;

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import me.bromen.podgo.app.dagger.AppScope;

/**
 * Created by jeff on 6/20/17.
 */

@Module
public class AppModule {

    private final Context context;

    public AppModule(Application application) {
        this.context = application.getApplicationContext();
    }

    @AppScope
    @Provides
    public Context context() {
        return this.context;
    }
}
