package me.bromen.podgo.fragments.mediaplayerbar.dagger;

import dagger.Component;
import me.bromen.podgo.app.dagger.AppComponent;
import me.bromen.podgo.fragments.mediaplayerbar.MediaplayerBarFragment;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.MediaplayerBarPresenter;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.contracts.MediaplayerBarModel;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.contracts.MediaplayerBarView;

/**
 * Created by Jeffrey on 7/7/2017.
 */

@MediaplayerBarScope
@Component(modules = {MediaplayerBarModule.class}, dependencies = {AppComponent.class})
public interface MediaplayerBarComponent {

    void inject(MediaplayerBarFragment mediaplayerBarFragment);
}
