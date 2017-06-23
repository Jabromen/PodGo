package me.bromen.podgo.activities.itunessearch.dagger;

import dagger.Component;
import me.bromen.podgo.activities.itunessearch.ItunesSearchActivity;
import me.bromen.podgo.app.dagger.AppComponent;

/**
 * Created by jeff on 6/22/17.
 */

@ItunesSearchScope
@Component(modules = {ItunesSearchModule.class}, dependencies = {AppComponent.class})
public interface ItunesSearchComponent {

    void inject(ItunesSearchActivity activity);
}
