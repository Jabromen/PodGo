package me.bromen.podgo.activities.feeddetail.dagger;

import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import me.bromen.podgo.activities.feeddetail.FeedDetailActivity;
import me.bromen.podgo.activities.feeddetail.mvp.FeedDetailModelImpl;
import me.bromen.podgo.activities.feeddetail.mvp.FeedDetailPresenter;
import me.bromen.podgo.activities.feeddetail.mvp.view.FeedDetailViewImpl;
import me.bromen.podgo.activities.feeddetail.mvp.contracts.FeedDetailModel;
import me.bromen.podgo.activities.feeddetail.mvp.contracts.FeedDetailView;
import me.bromen.podgo.app.storage.PodcastDbHelper;
import me.bromen.podgo.app.downloads.EpisodeDownloads;

/**
 * Created by jeff on 6/22/17.
 */

@Module
public class FeedDetailModule {

    private final FeedDetailActivity activity;
    private final long feedId;

    public FeedDetailModule(FeedDetailActivity activity, long feedId) {
        this.activity = activity;
        this.feedId = feedId;
    }

    @FeedDetailScope
    @Provides
    public FeedDetailView feedDetailView(Picasso picasso) {
        return new FeedDetailViewImpl(activity, picasso);
    }

    @FeedDetailScope
    @Provides
    public FeedDetailModel feedDetailModel(PodcastDbHelper dbHelper, EpisodeDownloads episodeDownloads) {
        return new FeedDetailModelImpl(activity, dbHelper, episodeDownloads);
    }

    @FeedDetailScope
    @Provides
    public FeedDetailPresenter feedDetailPresenter(FeedDetailView view, FeedDetailModel model) {
        return new FeedDetailPresenter(view, model, feedId);
    }
}
