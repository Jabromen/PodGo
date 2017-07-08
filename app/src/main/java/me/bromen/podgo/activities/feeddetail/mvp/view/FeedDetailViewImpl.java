package me.bromen.podgo.activities.feeddetail.mvp.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import me.bromen.podgo.R;
import me.bromen.podgo.activities.feeddetail.FeedDetailActivity;
import me.bromen.podgo.activities.feeddetail.mvp.contracts.FeedDetailView;
import me.bromen.podgo.extras.structures.Feed;
import me.bromen.podgo.extras.structures.FeedItem;
import me.bromen.podgo.fragments.mediaplayerbar.MediaplayerBarFragment;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.contracts.MediaplayerBarView;

/**
 * Created by jeff on 6/22/17.
 */

@SuppressLint("ViewConstructor")
public class FeedDetailViewImpl extends FrameLayout implements FeedDetailView {

    private static final String FRAGMENT_TAG = "feeddetailFragment";

    @BindView(R.id.recycler_feeddetail)
    RecyclerView feedView;

    @BindView(R.id.textview_feeddetail_error)
    TextView errorView;

    @BindView(R.id.toolbar_feeddetail)
    Toolbar toolbar;

    @BindView(R.id.mediaplayer_bar_feeddetail)
    FrameLayout mediaplayerBar;

    private final Activity activity;
    private final Picasso picasso;
    private EpisodeRecyclerAdapter adapter;
    private final ProgressDialog progressDialog = new ProgressDialog(getContext());

    public FeedDetailViewImpl(FeedDetailActivity activity, Picasso picasso) {
        super(activity);
        this.activity = activity;
        this.picasso = picasso;

        inflate(getContext(), R.layout.activity_feeddetail, this);

        ButterKnife.bind(this);

        initToolbar();
        initFeedView();
    }

    private void initToolbar() {

    }

    private void initFeedView() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        feedView.setLayoutManager(layoutManager);
        feedView.setHasFixedSize(true);

        adapter = new EpisodeRecyclerAdapter(picasso);
        feedView.setAdapter(adapter);
    }

    public void createMediaplayerBar() {
        MediaplayerBarFragment fragment = new MediaplayerBarFragment();
        FragmentManager fm = activity.getFragmentManager();
        fm.beginTransaction().add(R.id.mediaplayer_bar_feeddetail, fragment, FRAGMENT_TAG).commit();
    }

    public void destroyMediaplayerBar() {
        FragmentManager fm = activity.getFragmentManager();
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null) {
            fm.beginTransaction().remove(fragment).commit();
        }
    }

    @Override
    public void showFeed(Feed feed) {
        feedView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);

        toolbar.setTitle(feed.getTitle());
        adapter.updateList(feed);
    }

    @Override
    public void showError() {
        feedView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);

        toolbar.setTitle("Error");
        Toast.makeText(getContext(), "Error Loading Feed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading(boolean loading) {
        if (loading) {
            progressDialog.show();
            progressDialog.setMessage("Loading Feed");
        } else {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showMediaplayerBar(boolean show) {
        if (show) {
            mediaplayerBar.setVisibility(View.VISIBLE);
        } else {
            mediaplayerBar.setVisibility(View.GONE);
        }
    }

    @Override
    public Observable<FeedItem> observeItemTileClick() {
        return adapter.getTileClickedObservable();
    }

    @Override
    public Observable<FeedItem> observeItemActionClick() {
        return adapter.getActionClickedObservable();
    }
}
