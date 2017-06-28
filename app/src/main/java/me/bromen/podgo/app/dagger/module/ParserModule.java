package me.bromen.podgo.app.dagger.module;

import dagger.Module;
import dagger.Provides;
import me.bromen.podgo.app.parser.FeedParser;
import me.bromen.podgo.app.dagger.AppScope;
import me.bromen.podgo.app.storage.DbHelper;
import okhttp3.OkHttpClient;

/**
 * Created by jeff on 6/20/17.
 */

@Module
public class ParserModule {

    @AppScope
    @Provides
    public FeedParser feedParser(OkHttpClient okHttpClient, DbHelper dbHelper) {
        return new FeedParser(okHttpClient, dbHelper);
    }
}
