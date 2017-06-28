package me.bromen.podgo.app.dagger.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import me.bromen.podgo.app.dagger.AppScope;
import me.bromen.podgo.app.storage.DbHelper;

/**
 * Created by jeff on 6/20/17.
 */

@Module
public class DatabaseModule {

    @AppScope
    @Provides
    public DbHelper dbHelper(Context context) {
        return new DbHelper(context);
    }
}
