package me.bromen.podgo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import io.reactivex.Observable;
import me.bromen.podgo.activities.newfeed.mvp.NewFeedPresenter;
import me.bromen.podgo.activities.newfeed.mvp.contracts.NewFeedModel;
import me.bromen.podgo.activities.newfeed.mvp.contracts.NewFeedView;
import me.bromen.podgo.ext.structures.Feed;

/**
 * Created by jeff on 6/21/17.
 */

public class NewFeedPresenterTest {

    private NewFeedView view;
    private NewFeedModel model;
    private NewFeedPresenter presenter;

    private final String sampleUrl = "http://feed.com/example";
    private final Feed sampleFeed = new Feed();

    @Rule
    public TrampolineSchedulerRule trampolineSchedulerRule = new TrampolineSchedulerRule();

    @Before
    public void setUp() {
        view = Mockito.mock(NewFeedView.class);
        model = Mockito.mock(NewFeedModel.class);

        presenter = new NewFeedPresenter(view, model);

        Mockito.when(view.observeItunesButton()).thenReturn(Observable.never());
        Mockito.when(view.observeManualButton()).thenReturn(Observable.never());
    }

    @Test
    public void onObserveItunesButtonPressed() throws Exception {
        Mockito.when(view.observeItunesButton()).thenReturn(Observable.just(new Object()));

        presenter.onCreate();

        Mockito.verify(model).startItunesSearchActivity();
    }

    @Test
    public void onObserveManualButtonPressed() throws Exception {
        Mockito.when(view.observeManualButton()).thenReturn(Observable.just(sampleUrl));
        Mockito.when(model.downloadFeed(sampleUrl)).thenReturn("");

        presenter.onCreate();

        Mockito.verify(model).downloadFeed(sampleUrl);
    }

    @Test
    public void onObserveManualDownloadSuccess() throws Exception {
        final String successResponse = "";
        Mockito.when(view.observeManualButton()).thenReturn(Observable.just(sampleUrl));
        Mockito.when(model.downloadFeed(sampleUrl)).thenReturn("");

        presenter.onCreate();

        Mockito.verify(view).showDownloadSuccess();
    }

    @Test
    public void onObserveManualError() throws Exception {
        final String errorReason = "errorReason";
        Mockito.when(view.observeManualButton()).thenReturn(Observable.just(sampleUrl));
        Mockito.when(model.downloadFeed(sampleUrl)).thenReturn(errorReason);

        presenter.onCreate();

        Mockito.verify(view).showError(errorReason);
    }
}
