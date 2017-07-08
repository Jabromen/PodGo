package me.bromen.podgo.activities.mediacontrol.dagger;

import dagger.Component;
import me.bromen.podgo.activities.mediacontrol.MediaControlActivity;
import me.bromen.podgo.app.dagger.AppComponent;

/**
 * Created by jeff on 7/8/17.
 */

@MediaControlScope
@Component(modules = {MediaControlModule.class}, dependencies = {AppComponent.class})
public interface MediaControlComponent {

    void inject(MediaControlActivity mediaControlActivity);
}
