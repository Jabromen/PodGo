package me.bromen.podgo.activities.feeddetail.mvp;

import android.annotation.SuppressLint;
import android.widget.FrameLayout;

import com.squareup.picasso.Picasso;

import io.reactivex.Observable;
import me.bromen.podgo.activities.feeddetail.FeedDetailActivity;
import me.bromen.podgo.activities.feeddetail.mvp.contracts.FeedDetailView;
import me.bromen.podgo.ext.structures.Feed;
import me.bromen.podgo.ext.structures.FeedItem;

/**
 * Created by jeff on 6/22/17.
 */

@SuppressLint("ViewConstructor")
public class FeedDetailViewImpl extends FrameLayout implements FeedDetailView {

    private final Picasso picasso;

    public FeedDetailViewImpl(FeedDetailActivity activity, Picasso picasso) {
        super(activity);
        this.picasso = picasso;
    }

    @Override
    public void showFeed(Feed feed) {

    }

    @Override
    public void showError() {

    }

    @Override
    public void showLoading(boolean loading) {

    }

    @Override
    public Observable<FeedItem> observeItemTileClick() {
        return null;
    }

    @Override
    public Observable<FeedItem> observeItemDownloadClick() {
        return null;
    }
}
