package me.bromen.podgo.activities.home;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import me.bromen.podgo.activities.home.dagger.DaggerHomeComponent;
import me.bromen.podgo.activities.home.dagger.HomeModule;
import me.bromen.podgo.activities.home.mvp.HomePresenter;
import me.bromen.podgo.activities.home.mvp.contracts.HomeView;
import me.bromen.podgo.activities.home.mvp.view.HomeViewImpl;
import me.bromen.podgo.app.PodGoApplication;

public class MainActivity extends AppCompatActivity {

    @Inject
    HomeView view;

    @Inject
    HomePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerHomeComponent.builder()
                .appComponent(PodGoApplication.get(this).component())
                .homeModule(new HomeModule(this))
                .build()
                .inject(this);

        setContentView((HomeViewImpl) view);
        ((HomeViewImpl) view).setUpMediaplayerBar();
        PodGoApplication.get(this).component().mediaPlayerServiceController().bindService();

        presenter.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
        PodGoApplication.get(this).component().episodeDownloads().validateDownloads();
        PodGoApplication.get(this).component().episodeDownloads().registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PodGoApplication.get(this).component().episodeDownloads().unregisterReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        ((HomeViewImpl) view).destroyMediaplayerBar();
        if (isFinishing()) {
            PodGoApplication.get(this).component().mediaPlayerServiceController().unbindService();
        }
    }
}
