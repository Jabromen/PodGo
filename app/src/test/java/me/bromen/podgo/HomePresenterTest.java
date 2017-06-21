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
import rx.Observable;
import test.RxSchedulersOverrideRule;

/**
 * Created by jeff on 6/20/17.
 */

public class HomePresenterTest {

    private HomePresenter homePresenter;
    private HomeView homeView;
    private HomeModel homeModel;

    @Rule
    public RxSchedulersOverrideRule overrideRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {

        homeView = Mockito.mock(HomeView.class);
        homeModel = Mockito.mock(HomeModel.class);

        homePresenter = new HomePresenter(homeView, homeModel);
    }

    @Test
    public void onObserveLoadFeedsNoFeeds() {
        FeedList mockFeedList = new FeedList();
        Mockito.when(homeModel.loadFeeds()).thenReturn(Observable.just(mockFeedList));

        homePresenter.onCreate();

        InOrder inOrder = Mockito.inOrder(homeView);
        inOrder.verify(homeView).showLoading(true);
        inOrder.verify(homeView).showLoading(false);
        inOrder.verify(homeView).showNoFeeds();
    }

    @Test
    public void onObserveLoadFeedsWithFeeds() {
        Feed[] feeds = {new Feed(), new Feed(), new Feed()};
        FeedList mockFeedList = new FeedList(Arrays.asList(feeds));
        Mockito.when(homeModel.loadFeeds()).thenReturn(Observable.just(mockFeedList));

        homePresenter.onCreate();

        InOrder inOrder = Mockito.inOrder(homeView);
        inOrder.verify(homeView).showLoading(true);
        inOrder.verify(homeView).showLoading(false);
        inOrder.verify(homeView).showFeeds(mockFeedList);
    }
}
