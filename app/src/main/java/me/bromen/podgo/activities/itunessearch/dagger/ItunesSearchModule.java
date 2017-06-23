package me.bromen.podgo.activities.itunessearch.dagger;

import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import me.bromen.podgo.activities.itunessearch.ItunesSearchActivity;
import me.bromen.podgo.activities.itunessearch.mvp.model.ItunesSearchModelImpl;
import me.bromen.podgo.activities.itunessearch.mvp.ItunesSearchPresenter;
import me.bromen.podgo.activities.itunessearch.mvp.view.ItunesSearchViewImpl;
import me.bromen.podgo.activities.itunessearch.mvp.contracts.ItunesSearchModel;
import me.bromen.podgo.activities.itunessearch.mvp.contracts.ItunesSearchView;
import me.bromen.podgo.activities.itunessearch.mvp.model.ItunesApiService;
import me.bromen.podgo.app.parser.FeedParser;
import okhttp3.OkHttpClient;

/**
 * Created by jeff on 6/22/17.
 */

@Module
public class ItunesSearchModule {

    private final ItunesSearchActivity activity;

    public ItunesSearchModule(ItunesSearchActivity activity) {
        this.activity = activity;
    }

    @ItunesSearchScope
    @Provides
    public ItunesSearchView itunesSearchView(Picasso picasso) {
        return new ItunesSearchViewImpl(activity, picasso);
    }

    @ItunesSearchScope
    @Provides
    public ItunesSearchModel itunesSearchModel(FeedParser feedParser, ItunesApiService itunesApiService) {
        return new ItunesSearchModelImpl(feedParser, itunesApiService);
    }

    @ItunesSearchScope
    @Provides
    public ItunesSearchPresenter itunesSearchPresenter(ItunesSearchView view, ItunesSearchModel model) {
        return new ItunesSearchPresenter(view, model);
    }

    @ItunesSearchScope
    @Provides
    public ItunesApiService itunesApiService(OkHttpClient okHttpClient) {
        return new ItunesApiService(okHttpClient);
    }
}
