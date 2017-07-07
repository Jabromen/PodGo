package me.bromen.podgo.activities.itunessearch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import me.bromen.podgo.activities.itunessearch.dagger.DaggerItunesSearchComponent;
import me.bromen.podgo.activities.itunessearch.dagger.ItunesSearchModule;
import me.bromen.podgo.activities.itunessearch.mvp.ItunesSearchPresenter;
import me.bromen.podgo.activities.itunessearch.mvp.contracts.ItunesSearchView;
import me.bromen.podgo.activities.itunessearch.mvp.view.ItunesSearchViewImpl;
import me.bromen.podgo.app.PodGoApplication;

/**
 * Created by jeff on 6/22/17.
 */

public class ItunesSearchActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, ItunesSearchActivity.class);
        context.startActivity(intent);
    }

    @Inject
    ItunesSearchView view;

    @Inject
    ItunesSearchPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerItunesSearchComponent.builder()
                .appComponent(PodGoApplication.get(this).component())
                .itunesSearchModule(new ItunesSearchModule(this))
                .build()
                .inject(this);

        setContentView((ItunesSearchViewImpl) view);
        presenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
