package me.bromen.podgo.activities.feeddetail.mvp.view;

import android.annotation.SuppressLint;
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

/**
 * Created by jeff on 6/22/17.
 */

@SuppressLint("ViewConstructor")
public class FeedDetailViewImpl extends FrameLayout implements FeedDetailView {

    @BindView(R.id.recycler_feeddetail)
    RecyclerView feedView;

    @BindView(R.id.textview_feeddetail_error)
    TextView errorView;

    @BindView(R.id.toolbar_feeddetail)
    Toolbar toolbar;

    private final Picasso picasso;
    private EpisodeRecyclerAdapter adapter;
    private final ProgressDialog progressDialog = new ProgressDialog(getContext());

    public FeedDetailViewImpl(FeedDetailActivity activity, Picasso picasso) {
        super(activity);
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
    public Observable<FeedItem> observeItemTileClick() {
        return adapter.getTileClickedObservable();
    }

    @Override
    public Observable<FeedItem> observeItemDownloadClick() {
        return adapter.getDownloadClickedObservable();
    }
}
