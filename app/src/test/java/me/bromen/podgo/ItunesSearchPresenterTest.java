package me.bromen.podgo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import me.bromen.podgo.activities.itunessearch.mvp.ItunesSearchPresenter;
import me.bromen.podgo.activities.itunessearch.mvp.contracts.ItunesSearchModel;
import me.bromen.podgo.activities.itunessearch.mvp.contracts.ItunesSearchView;
import me.bromen.podgo.ext.structures.ItunesPodcast;

/**
 * Created by jeff on 6/22/17.
 */

public class ItunesSearchPresenterTest {

    private ItunesSearchView view;
    private ItunesSearchModel model;
    private ItunesSearchPresenter presenter;

    private final String successResponse = "";
    private final String errorResponse = "errorResponse";

    private final String sampleQuery = "sampleQuery";
    private final String sampleFeedUrl = "sampleFeedUrl";

    private final ItunesPodcast[] podcasts = {new ItunesPodcast(), new ItunesPodcast(), new ItunesPodcast()};
    private final List<ItunesPodcast> seachResults = new ArrayList<>((List<ItunesPodcast>) Arrays.asList(podcasts));
    private final List<ItunesPodcast> noResults = new ArrayList<>();

    @Rule
    public TrampolineSchedulerRule trampolineSchedulerRule = new TrampolineSchedulerRule();

    @Before
    public void setUp() {
        view = Mockito.mock(ItunesSearchView.class);
        model = Mockito.mock(ItunesSearchModel.class);

        presenter = new ItunesSearchPresenter(view, model);

        Mockito.when(view.observeSearchQuery()).thenReturn(Observable.never());
        Mockito.when(view.observeDownloadFeedClick()).thenReturn(Observable.never());
    }

    @Test
    public void onInitialLoadTest() throws Exception {

        presenter.onCreate();

        Mockito.verify(view).showNoSearchResults();
    }

    @Test
    public void onSearchQueryEnteredWithResultsTest() throws Exception {
        Mockito.when(view.observeSearchQuery()).thenReturn(Observable.just(sampleQuery));
        Mockito.when(model.searchItunes(sampleQuery)).thenReturn(seachResults);

        presenter.onCreate();

        Mockito.verify(model).searchItunes(sampleQuery);
        Mockito.verify(view).showLoadingResults(true);
        Mockito.verify(view).showLoadingResults(false);
        Mockito.verify(view).showSearchResults(seachResults);
    }

    @Test
    public void onSearchQueryEnteredNoResultsTest() throws Exception {
        Mockito.when(view.observeSearchQuery()).thenReturn(Observable.just(sampleQuery));
        Mockito.when(model.searchItunes(sampleQuery)).thenReturn(noResults);

        presenter.onCreate();

        Mockito.verify(model).searchItunes(sampleQuery);
        Mockito.verify(view).showLoadingResults(true);
        Mockito.verify(view).showLoadingResults(false);
        Mockito.verify(view, Mockito.times(2)).showNoSearchResults();
    }

    @Test
    public void onDownloadFeedSuccessTest() throws Exception {
        Mockito.when(view.observeSearchQuery()).thenReturn(Observable.just(sampleQuery));
        Mockito.when(model.searchItunes(sampleQuery)).thenReturn(seachResults);
        Mockito.when(view.observeDownloadFeedClick()).thenReturn(Observable.just(sampleFeedUrl));
        Mockito.when(model.downloadFeed(sampleFeedUrl)).thenReturn(successResponse);

        presenter.onCreate();

        Mockito.verify(model).downloadFeed(sampleFeedUrl);
        Mockito.verify(view).showLoadingFeed(true);
        Mockito.verify(view).showLoadingFeed(false);
        Mockito.verify(view).showDownloadSuccess();
    }

    @Test
    public void onDownloadFeedErrorTest() throws Exception {
        Mockito.when(view.observeSearchQuery()).thenReturn(Observable.just(sampleQuery));
        Mockito.when(model.searchItunes(sampleQuery)).thenReturn(seachResults);
        Mockito.when(view.observeDownloadFeedClick()).thenReturn(Observable.just(sampleFeedUrl));
        Mockito.when(model.downloadFeed(sampleFeedUrl)).thenReturn(errorResponse);

        presenter.onCreate();

        Mockito.verify(model).downloadFeed(sampleFeedUrl);
        Mockito.verify(view).showLoadingFeed(true);
        Mockito.verify(view).showLoadingFeed(false);
        Mockito.verify(view).showDownloadError(errorResponse);
    }
}
