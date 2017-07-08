package me.bromen.podgo.fragments.mediaplayerbar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import me.bromen.podgo.R;
import me.bromen.podgo.app.PodGoApplication;
import me.bromen.podgo.fragments.mediaplayerbar.dagger.DaggerMediaplayerBarComponent;
import me.bromen.podgo.fragments.mediaplayerbar.dagger.MediaplayerBarModule;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.MediaplayerBarPresenter;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.MediaplayerBarViewImpl;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.contracts.MediaplayerBarView;

/**
 * Created by Jeffrey on 7/7/2017.
 */

public class MediaplayerBarFragment extends Fragment {

    @Inject
    MediaplayerBarPresenter presenter;

    @Inject
    MediaplayerBarView view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerMediaplayerBarComponent.builder()
                .appComponent(PodGoApplication.get(getActivity()).component())
                .mediaplayerBarModule(new MediaplayerBarModule(getActivity()))
                .build()
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = ((MediaplayerBarViewImpl) view).inflateView();
        presenter.onCreate();
        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
