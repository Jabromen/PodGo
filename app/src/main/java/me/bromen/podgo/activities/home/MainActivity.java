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
        presenter.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
