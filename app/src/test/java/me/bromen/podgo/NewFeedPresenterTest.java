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

        presenter.onCreate();

        Mockito.verify(model).downloadFeed(sampleUrl);
    }

    @Test
    public void onObserveManualDownloadSuccess() throws Exception {
        Mockito.when(view.observeManualButton()).thenReturn(Observable.just(sampleUrl));
        Mockito.when(model.downloadFeed(sampleUrl)).thenReturn(sampleFeed);

        presenter.onCreate();

        Mockito.verify(view).showDownloadSuccess();
        Mockito.verify(model).saveFeed(sampleFeed);
    }

    @Test
    public void onObserveManualDownloadError() throws Exception {
        final String downloadError = "downloadError";
        Mockito.when(view.observeManualButton()).thenReturn(Observable.just(sampleUrl));
        Mockito.when(model.downloadFeed(sampleUrl)).thenThrow(new Exception(downloadError));

        presenter.onCreate();

        Mockito.verify(view).showDownloadError(downloadError);
        Mockito.verify(model, Mockito.never()).saveFeed(Mockito.any());
    }

    @Test
    public void onObserveManualSaveSuccess() throws Exception {
        Mockito.when(view.observeManualButton()).thenReturn(Observable.just(sampleUrl));
        Mockito.when(model.downloadFeed(sampleUrl)).thenReturn(sampleFeed);
        Mockito.when(model.saveFeed(sampleFeed)).thenReturn(true);

        presenter.onCreate();

        Mockito.verify(view).showDownloadSuccess();
        Mockito.verify(view).showSaveSuccess();
    }

    @Test
    public void onObserveManualAlreadySaved() throws Exception {
        Mockito.when(view.observeManualButton()).thenReturn(Observable.just(sampleUrl));
        Mockito.when(model.downloadFeed(sampleUrl)).thenReturn(sampleFeed);
        Mockito.when(model.saveFeed(sampleFeed)).thenReturn(false);

        presenter.onCreate();

        Mockito.verify(view).showDownloadSuccess();
        Mockito.verify(view).showSaveError(Mockito.anyString());
    }

    @Test
    public void onObserveManualSaveError() throws Exception {
        final String saveError = "saveError";
        Mockito.when(view.observeManualButton()).thenReturn(Observable.just(sampleUrl));
        Mockito.when(model.downloadFeed(sampleUrl)).thenReturn(sampleFeed);
        Mockito.when(model.saveFeed(sampleFeed)).thenThrow(new Exception(saveError));

        presenter.onCreate();

        Mockito.verify(view).showDownloadSuccess();
        Mockito.verify(view).showSaveError(saveError);
    }
}
