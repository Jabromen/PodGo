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
import me.bromen.podgo.extras.structures.Feed;
import me.bromen.podgo.extras.structures.FeedList;
import io.reactivex.Observable;

/**
 * Created by jeff on 6/20/17.
 */

public class HomePresenterTest {

    private HomePresenter homePresenter;
    private HomeView homeView;
    private HomeModel homeModel;

    private final Feed[] feeds = {new Feed(), new Feed(), new Feed()};
    private final FeedList mockFeedList = new FeedList(Arrays.asList(feeds));

    @Rule
    public TrampolineSchedulerRule trampolineSchedulerRule = new TrampolineSchedulerRule();

    @Before
    public void setUp() throws Exception {

        homeView = Mockito.mock(HomeView.class);
        homeModel = Mockito.mock(HomeModel.class);

        homePresenter = new HomePresenter(homeView, homeModel);

        Mockito.when(homeView.observeMenuItemClick()).thenReturn(Observable.never());
        Mockito.when(homeView.observeFeedTileClick()).thenReturn(Observable.never());
        Mockito.when(homeView.observeFeedOptionsClick()).thenReturn(Observable.never());
        Mockito.when(homeView.observeFeedOptionMenuClick()).thenReturn(Observable.never());

        Mockito.when(homeModel.loadFeeds()).thenReturn(mockFeedList);
    }

    @Test
    public void onObserveLoadFeedsNoFeeds() throws Exception {
        Mockito.when(homeModel.loadFeeds()).thenReturn(new FeedList());

        homePresenter.onCreate();
        homePresenter.onResume();

        InOrder inOrder = Mockito.inOrder(homeView);
        inOrder.verify(homeView).showLoading(true);
        inOrder.verify(homeView).showLoading(false);
        inOrder.verify(homeView).showNoFeeds();
        Mockito.verify(homeView, Mockito.never()).showError();
    }

    @Test
    public void onObserveLoadFeedsWithFeeds() throws Exception {

        homePresenter.onCreate();
        homePresenter.onResume();

        InOrder inOrder = Mockito.inOrder(homeView);
        inOrder.verify(homeView).showLoading(true);
        inOrder.verify(homeView).showLoading(false);
        inOrder.verify(homeView).showFeeds(mockFeedList);
        Mockito.verify(homeView, Mockito.never()).showError();
    }

    @Test
    public void onObserveLoadFeedsWithError() throws Exception {
        Mockito.when(homeModel.loadFeeds()).thenThrow(new Exception());

        homePresenter.onCreate();
        homePresenter.onResume();

        InOrder inOrder = Mockito.inOrder(homeView);
        inOrder.verify(homeView).showLoading(true);
        inOrder.verify(homeView).showLoading(false);
        inOrder.verify(homeView).showError();
    }

    @Test
    public void onNewFeedMenuItemClicked() throws Exception {
        Mockito.when(homeView.observeMenuItemClick()).thenReturn(Observable.just(R.id.action_new_podcast));

        homePresenter.onCreate();
        homePresenter.onResume();

        Mockito.verify(homeModel).startNewFeedActivity();
        Mockito.verify(homeView, Mockito.never()).showError();
    }

    @Test
    public void onOptionsMenuItemClicked() throws Exception {
        Mockito.when(homeView.observeMenuItemClick()).thenReturn(Observable.just(R.id.action_settings));

        homePresenter.onCreate();
        homePresenter.onResume();

        Mockito.verify(homeModel).startOptionsActivity();
        Mockito.verify(homeView, Mockito.never()).showError();
    }

    @Test
    public void onFeedTileClicked() throws Exception {
        Feed feed = new Feed();
        Mockito.when(homeView.observeFeedTileClick()).thenReturn(Observable.just(feed));

        homePresenter.onCreate();
        homePresenter.onResume();

        Mockito.verify(homeModel).startFeedDetailActivity(feed.getId());
        Mockito.verify(homeView, Mockito.never()).showError();
    }

    @Test
    public void onFeedOptionsClicked() throws Exception {
        Feed feed = new Feed();
        Mockito.when(homeView.observeFeedOptionsClick()).thenReturn(Observable.just(feed));

        homePresenter.onCreate();
        homePresenter.onResume();

        Mockito.verify(homeView).showFeedOptions();
        Mockito.verify(homeView, Mockito.never()).showError();
    }

    @Test
    public void onFeedOptionsRefreshClickedSuccess() throws Exception {
        Feed feed = new Feed();
        final int newEps = 3;
        Mockito.when(homeView.observeFeedOptionsClick()).thenReturn(Observable.just(feed));
        Mockito.when(homeView.observeFeedOptionMenuClick()).thenReturn(Observable.just(R.id.action_refresh_feed));
        Mockito.when(homeModel.refreshFeed(feed)).thenReturn(newEps);

        homePresenter.onCreate();
        homePresenter.onResume();

        Mockito.verify(homeView).showFeedOptions();
        Mockito.verify(homeModel).refreshFeed(feed);
        Mockito.verify(homeView).showNewEpisodes(newEps);
        Mockito.verify(homeView, Mockito.never()).showError();
    }

    @Test
    public void onFeedOptionsRefreshClickedError() throws Exception {
        Feed feed = new Feed();
        Mockito.when(homeView.observeFeedOptionsClick()).thenReturn(Observable.just(feed));
        Mockito.when(homeView.observeFeedOptionMenuClick()).thenReturn(Observable.just(R.id.action_refresh_feed));
        Mockito.when(homeModel.refreshFeed(feed)).thenThrow(new Exception());

        homePresenter.onCreate();
        homePresenter.onResume();

        Mockito.verify(homeView).showFeedOptions();
        Mockito.verify(homeModel).refreshFeed(feed);
        Mockito.verify(homeView).showError();
    }

    @Test
    public void onFeedDeleteClickedSuccess() throws Exception {
        Feed feed = new Feed();
        Mockito.when(homeView.observeFeedOptionsClick()).thenReturn(Observable.just(feed));
        Mockito.when(homeView.observeFeedOptionMenuClick()).thenReturn(Observable.just(R.id.action_delete_feed));
        Mockito.when(homeModel.deleteFeed(feed)).thenReturn(true);

        homePresenter.onCreate();
        homePresenter.onResume();

        Mockito.verify(homeView).showFeedOptions();
        Mockito.verify(homeModel).deleteFeed(feed);
        Mockito.verify(homeView, Mockito.times(2)).showFeeds(mockFeedList);
        Mockito.verify(homeView, Mockito.never()).showError();
    }

    @Test
    public void onFeedDeleteClickedError() throws Exception {
        Feed feed = new Feed();
        Mockito.when(homeView.observeFeedOptionsClick()).thenReturn(Observable.just(feed));
        Mockito.when(homeView.observeFeedOptionMenuClick()).thenReturn(Observable.just(R.id.action_delete_feed));
        Mockito.when(homeModel.deleteFeed(feed)).thenThrow(new Exception());

        homePresenter.onCreate();
        homePresenter.onResume();

        Mockito.verify(homeView).showFeedOptions();
        Mockito.verify(homeModel).deleteFeed(feed);
        Mockito.verify(homeView).showError();
        Mockito.verify(homeView, Mockito.times(1)).showFeeds(mockFeedList);
    }
}
