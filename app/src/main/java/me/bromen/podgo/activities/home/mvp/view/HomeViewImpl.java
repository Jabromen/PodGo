package me.bromen.podgo.activities.home.mvp.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.bromen.podgo.R;
import me.bromen.podgo.activities.home.mvp.contracts.HomeView;
import me.bromen.podgo.structures.FeedList;
import me.bromen.podgo.utilities.DisplayUtils;

/**
 * Created by jeff on 6/20/17.
 */

@SuppressLint("ViewConstructor")
public class HomeViewImpl extends FrameLayout implements HomeView {

    @BindView(R.id.recycler_main)
    RecyclerView feedView;

    @BindView(R.id.textview_main_nopodcasts)
    TextView noFeedsView;

    @BindView(R.id.toolbar_main)
    Toolbar toolbar;

    private final Activity activity;
    private final PodcastRecyclerAdapter adapter = new PodcastRecyclerAdapter();
    private final ProgressDialog progressDialog = new ProgressDialog(getContext());

    public HomeViewImpl(Activity activity) {
        super(activity);
        this.activity = activity;

        inflate(getContext(), R.layout.activity_main, this);

        ButterKnife.bind(this);

        setUpFeedView();
    }

    private void setUpFeedView() {

        RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(getContext(), DisplayUtils.calculateNoOfColumns(getContext(), 125, 3));
        feedView.setLayoutManager(layoutManager);
        feedView.setHasFixedSize(true);
        feedView.setAdapter(adapter);
    }

    @Override
    public void showNoFeeds() {
        feedView.setVisibility(View.GONE);
        noFeedsView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showFeeds(FeedList feedList) {
        feedView.setVisibility(View.VISIBLE);
        noFeedsView.setVisibility(View.GONE);
        adapter.updateList(feedList);
    }

    @Override
    public void showLoading(boolean loading) {
        if (loading) {
            progressDialog.show();
            progressDialog.setMessage("Loading Feeds");
        } else {
            progressDialog.dismiss();
        }
    }
}
