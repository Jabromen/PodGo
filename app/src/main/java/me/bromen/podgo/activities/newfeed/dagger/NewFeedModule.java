package me.bromen.podgo.activities.newfeed.dagger;

import dagger.Module;
import dagger.Provides;
import me.bromen.podgo.activities.newfeed.NewFeedActivity;
import me.bromen.podgo.activities.newfeed.mvp.NewFeedModelImpl;
import me.bromen.podgo.activities.newfeed.mvp.NewFeedPresenter;
import me.bromen.podgo.activities.newfeed.mvp.NewFeedViewImpl;
import me.bromen.podgo.activities.newfeed.mvp.contracts.NewFeedModel;
import me.bromen.podgo.activities.newfeed.mvp.contracts.NewFeedView;
import me.bromen.podgo.app.parser.FeedParser;

/**
 * Created by jeff on 6/21/17.
 */

@Module
public class NewFeedModule {

    private final NewFeedActivity activity;

    public NewFeedModule(NewFeedActivity activity) {
        this.activity = activity;
    }

    @Provides
    @NewFeedScope
    public NewFeedView newFeedView() {
        return new NewFeedViewImpl(activity);
    }

    @Provides
    @NewFeedScope
    public NewFeedModel newFeedModel(FeedParser feedParser) {
        return new NewFeedModelImpl(activity, feedParser);
    }

    @Provides
    @NewFeedScope
    public NewFeedPresenter newFeedPresenter(NewFeedView view, NewFeedModel model) {
        return new NewFeedPresenter(view, model);
    }
}
