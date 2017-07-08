package me.bromen.podgo.activities.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import me.bromen.podgo.activities.home.dagger.DaggerHomeComponent;
import me.bromen.podgo.activities.home.dagger.HomeModule;
import me.bromen.podgo.activities.home.mvp.HomePresenter;
import me.bromen.podgo.activities.home.mvp.contracts.HomeView;
import me.bromen.podgo.activities.home.mvp.view.HomeViewImpl;
import me.bromen.podgo.app.PodGoApplication;
import me.bromen.podgo.app.downloads.EpisodeDownloads;
import me.bromen.podgo.app.mediaplayer.MediaPlayerServiceController;

public class MainActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Inject
    HomeView view;

    @Inject
    HomePresenter presenter;

    @Inject
    MediaPlayerServiceController controller;

    @Inject
    EpisodeDownloads episodeDownloads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerHomeComponent.builder()
                .appComponent(PodGoApplication.get(this).component())
                .homeModule(new HomeModule(this))
                .build()
                .inject(this);

        setContentView((HomeViewImpl) view);

        if (savedInstanceState == null) {
            ((HomeViewImpl) view).createMediaplayerBar();
            if (!controller.isBound()) {
                controller.bindService();
            }
            episodeDownloads.registerReceiver();
            episodeDownloads.validateDownloads();
        }

        presenter.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            ((HomeViewImpl) view).destroyMediaplayerBar();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        if (isFinishing()) {
            if (controller.isBound()) {
                controller.unbindService();
            }
            episodeDownloads.unregisterReceiver();
        }
    }
}
