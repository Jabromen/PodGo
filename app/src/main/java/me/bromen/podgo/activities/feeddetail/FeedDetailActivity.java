package me.bromen.podgo.activities.feeddetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import me.bromen.podgo.activities.feeddetail.dagger.DaggerFeedDetailComponent;
import me.bromen.podgo.activities.feeddetail.dagger.FeedDetailModule;
import me.bromen.podgo.activities.feeddetail.mvp.FeedDetailPresenter;
import me.bromen.podgo.activities.feeddetail.mvp.FeedDetailViewImpl;
import me.bromen.podgo.activities.feeddetail.mvp.contracts.FeedDetailView;
import me.bromen.podgo.app.PodGoApplication;

/**
 * Created by jeff on 6/22/17.
 */

public class FeedDetailActivity extends AppCompatActivity {

    public static String ID = "id";

    public static void start(Context context, long id) {
        Intent intent = new Intent(context, FeedDetailActivity.class);
        intent.putExtra(ID, id);
        context.startActivity(intent);
    }

    @Inject
    FeedDetailView view;

    @Inject
    FeedDetailPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long id = getIntent().getLongExtra(ID, 0);

        DaggerFeedDetailComponent.builder()
                .appComponent(PodGoApplication.get(this).component())
                .feedDetailModule(new FeedDetailModule(this, id))
                .build()
                .inject(this);

        setContentView((FeedDetailViewImpl) view);
        presenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
