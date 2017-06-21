package me.bromen.podgo.app.dagger.module;

import dagger.Module;
import dagger.Provides;
import me.bromen.podgo.FeedParser;
import me.bromen.podgo.app.dagger.AppScope;
import okhttp3.OkHttpClient;

/**
 * Created by jeff on 6/20/17.
 */

@Module
public class ParserModule {

    @AppScope
    @Provides
    public FeedParser feedParser(OkHttpClient okHttpClient) {
        return new FeedParser(okHttpClient);
    }
}
