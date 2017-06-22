package me.bromen.podgo.activities.newfeed.dagger;

import dagger.Component;
import me.bromen.podgo.activities.newfeed.NewFeedActivity;
import me.bromen.podgo.app.dagger.AppComponent;

/**
 * Created by jeff on 6/21/17.
 */

@NewFeedScope
@Component(modules = { NewFeedModule.class }, dependencies = { AppComponent.class })
public interface NewFeedComponent {

    void inject(NewFeedActivity newFeedActivity);
}
