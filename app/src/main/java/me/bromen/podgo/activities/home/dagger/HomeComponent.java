package me.bromen.podgo.activities.home.dagger;

import dagger.Component;
import me.bromen.podgo.activities.home.MainActivity;
import me.bromen.podgo.app.dagger.AppComponent;

/**
 * Created by jeff on 6/20/17.
 */

@HomeScope
@Component(modules = { HomeModule.class }, dependencies = { AppComponent.class })
public interface HomeComponent {

    void inject(MainActivity mainActivity);
}
