package me.bromen.podgo.activities.newfeed;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import me.bromen.podgo.activities.newfeed.dagger.DaggerNewFeedComponent;
import me.bromen.podgo.activities.newfeed.dagger.NewFeedModule;
import me.bromen.podgo.activities.newfeed.mvp.NewFeedPresenter;
import me.bromen.podgo.activities.newfeed.mvp.NewFeedViewImpl;
import me.bromen.podgo.activities.newfeed.mvp.contracts.NewFeedView;
import me.bromen.podgo.app.PodGoApplication;

/**
 * Created by jeff on 6/21/17.
 */

public class NewFeedActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, NewFeedActivity.class);
        context.startActivity(intent);
    }

    @Inject
    NewFeedView view;

    @Inject
    NewFeedPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerNewFeedComponent.builder()
                .appComponent(PodGoApplication.get(this).component())
                .newFeedModule(new NewFeedModule(this))
                .build()
                .inject(this);

        setContentView((NewFeedViewImpl) view);
        presenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
