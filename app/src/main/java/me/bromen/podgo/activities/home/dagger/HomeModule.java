package me.bromen.podgo.activities.home.dagger;

import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import me.bromen.podgo.activities.home.MainActivity;
import me.bromen.podgo.activities.home.mvp.HomeModelImpl;
import me.bromen.podgo.activities.home.mvp.HomePresenter;
import me.bromen.podgo.activities.home.mvp.contracts.HomeModel;
import me.bromen.podgo.activities.home.mvp.contracts.HomeView;
import me.bromen.podgo.activities.home.mvp.view.HomeViewImpl;
import me.bromen.podgo.app.storage.PodcastDbHelper;

/**
 * Created by jeff on 6/20/17.
 */

@Module
public class HomeModule {

    private final MainActivity activity;

    public HomeModule(MainActivity activity) {
        this.activity = activity;
    }

    @Provides
    @HomeScope
    public HomeView view(Picasso picasso) {
        return new HomeViewImpl(activity, picasso);
    }

    @Provides
    @HomeScope
    public HomeModel model(PodcastDbHelper dbHelper) {
        return new HomeModelImpl(activity, dbHelper);
    }

    @Provides
    @HomeScope
    public HomePresenter presenter(HomeView view, HomeModel model) {
        return new HomePresenter(view, model);
    }
}
