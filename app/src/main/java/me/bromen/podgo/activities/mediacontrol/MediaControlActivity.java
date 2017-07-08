package me.bromen.podgo.activities.mediacontrol;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import javax.inject.Inject;

import me.bromen.podgo.activities.home.MainActivity;
import me.bromen.podgo.activities.mediacontrol.dagger.DaggerMediaControlComponent;
import me.bromen.podgo.activities.mediacontrol.dagger.MediaControlModule;
import me.bromen.podgo.activities.mediacontrol.mvp.MediaControlPresenter;
import me.bromen.podgo.activities.mediacontrol.mvp.MediaControlViewImpl;
import me.bromen.podgo.activities.mediacontrol.mvp.contracts.MediaControlView;
import me.bromen.podgo.app.PodGoApplication;
import me.bromen.podgo.app.mediaplayer.MediaPlayerServiceController;

/**
 * Created by jeff on 7/8/17.
 */

public class MediaControlActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, MediaControlActivity.class);
        context.startActivity(intent);
    }

    @Inject
    MediaControlPresenter presenter;

    @Inject
    MediaControlView view;

    @Inject
    MediaPlayerServiceController controller;

    private boolean boundController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerMediaControlComponent.builder()
                .appComponent(PodGoApplication.get(this).component())
                .mediaControlModule(new MediaControlModule(this))
                .build()
                .inject(this);

        setContentView((MediaControlViewImpl) view);

        if (savedInstanceState != null) {
            boundController = savedInstanceState.getBoolean("BOUND");
        } else {
            // If starting activity from notification after MainActivity closed, bind media service
            if (!controller.isBound()) {
                controller.bindService();
                boundController = true;
            }
        }

        presenter.onCreate();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("BOUND", boundController);
    }

    @Override
    public void onBackPressed() {
        if (boundController) {
            MainActivity.start(this);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
