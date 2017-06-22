package me.bromen.podgo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Arrays;

import me.bromen.podgo.activities.home.mvp.HomePresenter;
import me.bromen.podgo.activities.home.mvp.contracts.HomeModel;
import me.bromen.podgo.activities.home.mvp.contracts.HomeView;
import me.bromen.podgo.structures.Feed;
import me.bromen.podgo.structures.FeedList;
import io.reactivex.Observable;

/**
 * Created by jeff on 6/20/17.
 */

public class HomePresenterTest {

    private HomePresenter homePresenter;
    private HomeView homeView;
    private HomeModel homeModel;

    @Rule
    public TrampolineSchedulerRule trampolineSchedulerRule = new TrampolineSchedulerRule();

    @Before
    public void setUp() throws Exception {

        homeView = Mockito.mock(HomeView.class);
        homeModel = Mockito.mock(HomeModel.class);

        homePresenter = new HomePresenter(homeView, homeModel);

        Mockito.when(homeView.observeMenuItemClick()).thenReturn(Observable.never());
    }

    @Test
    public void onObserveLoadFeedsNoFeeds() throws Exception {
        FeedList mockFeedList = new FeedList();
        Mockito.when(homeModel.loadFeeds()).thenReturn(mockFeedList);

        homePresenter.onCreate();

        InOrder inOrder = Mockito.inOrder(homeView);
        inOrder.verify(homeView).showLoading(true);
        inOrder.verify(homeView).showLoading(false);
        inOrder.verify(homeView).showNoFeeds();
    }

    @Test
    public void onObserveLoadFeedsWithFeeds() throws Exception {
        Feed[] feeds = {new Feed(), new Feed(), new Feed()};
        FeedList mockFeedList = new FeedList(Arrays.asList(feeds));
        Mockito.when(homeModel.loadFeeds()).thenReturn(mockFeedList);

        homePresenter.onCreate();

        InOrder inOrder = Mockito.inOrder(homeView);
        inOrder.verify(homeView).showLoading(true);
        inOrder.verify(homeView).showLoading(false);
        inOrder.verify(homeView).showFeeds(mockFeedList);
    }

    @Test
    public void onObserveLoadFeedsWithError() throws Exception {
        Mockito.when(homeModel.loadFeeds()).thenThrow(new Exception());

        homePresenter.onCreate();

        InOrder inOrder = Mockito.inOrder(homeView);
        inOrder.verify(homeView).showLoading(true);
        inOrder.verify(homeView).showLoading(false);
        inOrder.verify(homeView).showError();
    }

    @Test
    public void onNewFeedMenuItemClicked() throws Exception {
        Mockito.when(homeView.observeMenuItemClick()).thenReturn(Observable.just(R.id.action_new_podcast));

        homePresenter.onCreate();

        Mockito.verify(homeModel).startNewFeedActivity();
    }

    @Test
    public void onOptionsMenuItemClicked() throws Exception {
        Mockito.when(homeView.observeMenuItemClick()).thenReturn(Observable.just(R.id.action_settings));

        homePresenter.onCreate();

        Mockito.verify(homeModel).startOptionsActivity();
    }
}
