package me.bromen.podgo.activities.home.mvp.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.bromen.podgo.R;
import me.bromen.podgo.activities.home.MainActivity;
import me.bromen.podgo.activities.home.mvp.contracts.HomeView;
import me.bromen.podgo.ext.structures.Feed;
import me.bromen.podgo.ext.structures.FeedList;
import me.bromen.podgo.ext.utilities.DisplayUtils;
import io.reactivex.Observable;

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

    private final Picasso picasso;
    private PodcastRecyclerAdapter adapter;
    private final ProgressDialog progressDialog = new ProgressDialog(getContext());

    public HomeViewImpl(MainActivity activity, Picasso picasso) {
        super(activity);
        this.picasso = picasso;

        inflate(getContext(), R.layout.activity_main, this);

        ButterKnife.bind(this);

        initToolbar();
        initFeedView();
    }

    private void initToolbar() {

        toolbar.setTitle("Podcasts");
        toolbar.inflateMenu(R.menu.menu_main);
    }

    private void initFeedView() {

        RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(getContext(), DisplayUtils.calculateNoOfColumns(getContext(), 125, 3));
        feedView.setLayoutManager(layoutManager);
        feedView.setHasFixedSize(true);

        adapter = new PodcastRecyclerAdapter(picasso);
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

    @Override
    public void showError() {
        Toast.makeText(getContext(), "Error Loading Feeds", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFeedOptions(long id) {
        Toast.makeText(getContext(), "Feed Options - " + id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Observable<Integer> observeMenuItemClick() {
        return RxToolbar.itemClicks(toolbar).map(MenuItem::getItemId);
    }

    @Override
    public Observable<Feed> observeFeedTileClick() {
        return adapter.getTileClickedObservable();
    }

    @Override
    public Observable<Feed> observeFeedOptionsClick() {
        return adapter.getOptionsClickedObservable();
    }
}
