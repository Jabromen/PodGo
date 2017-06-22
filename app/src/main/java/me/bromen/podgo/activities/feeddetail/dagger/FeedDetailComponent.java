package me.bromen.podgo.activities.feeddetail.dagger;

import dagger.Component;
import me.bromen.podgo.activities.feeddetail.FeedDetailActivity;
import me.bromen.podgo.app.dagger.AppComponent;

/**
 * Created by jeff on 6/22/17.
 */

@FeedDetailScope
@Component(modules = { FeedDetailModule.class }, dependencies = { AppComponent.class })
public interface FeedDetailComponent {

    void inject(FeedDetailActivity activity);
}
