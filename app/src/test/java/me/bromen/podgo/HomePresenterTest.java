package me.bromen.podgo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import me.bromen.podgo.activities.home.mvp.HomePresenter;
import me.bromen.podgo.activities.home.mvp.contracts.HomeModel;
import me.bromen.podgo.activities.home.mvp.contracts.HomeView;
import me.bromen.podgo.app.mediaplayer.MediaPlayerService;
import me.bromen.podgo.extras.structures.Feed;

/**
 * Created by jeff on 6/20/17.
 */

public class HomePresenterTest {

    private HomePresenter presenter;
    private HomeView view;
    private HomeModel model;

    private final Feed[] feeds = {new Feed(), new Feed(), new Feed()};
    private final List<Feed> mockFeedList = new ArrayList<>(Arrays.asList(feeds));

    @Rule
    public TrampolineSchedulerRule trampolineSchedulerRule = new TrampolineSchedulerRule();

    @Before
    public void setUp() throws Exception {

        view = Mockito.mock(HomeView.class);
        model = Mockito.mock(HomeModel.class);

        presenter = new HomePresenter(view, model);

        Mockito.when(view.observeMenuItemClick()).thenReturn(Observable.never());
        Mockito.when(view.observeFeedTileClick()).thenReturn(Observable.never());
        Mockito.when(view.observeFeedOptionsClick()).thenReturn(Observable.never());
        Mockito.when(view.observeFeedOptionMenuClick()).thenReturn(Observable.never());

        Mockito.when(model.loadFeeds()).thenReturn(mockFeedList);
        Mockito.when(model.getInitialMediaState()).thenReturn(MediaPlayerService.PLAYBACK_STOPPED);
        Mockito.when(model.observeMediaState()).thenReturn(Observable.never());
    }

    @Test
    public void onObserveLoadFeedsNoFeeds() throws Exception {
        Mockito.when(model.loadFeeds()).thenReturn(new ArrayList<>());

        presenter.onCreate();
        presenter.onResume();

        InOrder inOrder = Mockito.inOrder(view);
        inOrder.verify(view).showLoading(true);
        inOrder.verify(view).showLoading(false);
        inOrder.verify(view).showNoFeeds();
        Mockito.verify(view, Mockito.never()).showError();
        Mockito.verify(view).showMediaplayerBar(false);
    }

    @Test
    public void onObserveLoadFeedsWithFeeds() throws Exception {

        presenter.onCreate();
        presenter.onResume();

        InOrder inOrder = Mockito.inOrder(view);
        inOrder.verify(view).showLoading(true);
        inOrder.verify(view).showLoading(false);
        inOrder.verify(view).showFeeds(mockFeedList);
        Mockito.verify(view, Mockito.never()).showError();
    }

    @Test
    public void onObserveLoadFeedsWithError() throws Exception {
        Mockito.when(model.loadFeeds()).thenThrow(new Exception());

        presenter.onCreate();
        presenter.onResume();

        InOrder inOrder = Mockito.inOrder(view);
        inOrder.verify(view).showLoading(true);
        inOrder.verify(view).showLoading(false);
        inOrder.verify(view).showError();
    }

    @Test
    public void onNewFeedMenuItemClicked() throws Exception {
        Mockito.when(view.observeMenuItemClick()).thenReturn(Observable.just(R.id.action_new_podcast));

        presenter.onCreate();
        presenter.onResume();

        Mockito.verify(model).startNewFeedActivity();
        Mockito.verify(view, Mockito.never()).showError();
    }

    @Test
    public void onOptionsMenuItemClicked() throws Exception {
        Mockito.when(view.observeMenuItemClick()).thenReturn(Observable.just(R.id.action_settings));

        presenter.onCreate();
        presenter.onResume();

        Mockito.verify(model).startOptionsActivity();
        Mockito.verify(view, Mockito.never()).showError();
    }

    @Test
    public void onFeedTileClicked() throws Exception {
        Feed feed = new Feed();
        Mockito.when(view.observeFeedTileClick()).thenReturn(Observable.just(feed));

        presenter.onCreate();
        presenter.onResume();

        Mockito.verify(model).startFeedDetailActivity(feed.getId());
        Mockito.verify(view, Mockito.never()).showError();
    }

    @Test
    public void onFeedOptionsClicked() throws Exception {
        Feed feed = new Feed();
        Mockito.when(view.observeFeedOptionsClick()).thenReturn(Observable.just(feed));

        presenter.onCreate();
        presenter.onResume();

        Mockito.verify(view).showFeedOptions();
        Mockito.verify(view, Mockito.never()).showError();
    }

    @Test
    public void onFeedOptionsRefreshClickedSuccess() throws Exception {
        Feed feed = new Feed();
        final int newEps = 3;
        Mockito.when(view.observeFeedOptionsClick()).thenReturn(Observable.just(feed));
        Mockito.when(view.observeFeedOptionMenuClick()).thenReturn(Observable.just(R.id.action_refresh_feed));
        Mockito.when(model.refreshFeed(feed)).thenReturn(newEps);

        presenter.onCreate();
        presenter.onResume();

        Mockito.verify(view).showFeedOptions();
        Mockito.verify(model).refreshFeed(feed);
        Mockito.verify(view).showNewEpisodes(newEps);
        Mockito.verify(view, Mockito.never()).showError();
    }

    @Test
    public void onFeedOptionsRefreshClickedError() throws Exception {
        Feed feed = new Feed();
        Mockito.when(view.observeFeedOptionsClick()).thenReturn(Observable.just(feed));
        Mockito.when(view.observeFeedOptionMenuClick()).thenReturn(Observable.just(R.id.action_refresh_feed));
        Mockito.when(model.refreshFeed(feed)).thenThrow(new Exception());

        presenter.onCreate();
        presenter.onResume();

        Mockito.verify(view).showFeedOptions();
        Mockito.verify(model).refreshFeed(feed);
        Mockito.verify(view).showError();
    }

    @Test
    public void onFeedDeleteClickedSuccess() throws Exception {
        Feed feed = new Feed();
        Mockito.when(view.observeFeedOptionsClick()).thenReturn(Observable.just(feed));
        Mockito.when(view.observeFeedOptionMenuClick()).thenReturn(Observable.just(R.id.action_delete_feed));
        Mockito.when(model.deleteFeed(feed)).thenReturn(true);

        presenter.onCreate();
        presenter.onResume();

        Mockito.verify(view).showFeedOptions();
        Mockito.verify(model).deleteFeed(feed);
        Mockito.verify(view, Mockito.times(2)).showFeeds(mockFeedList);
        Mockito.verify(view, Mockito.never()).showError();
    }

    @Test
    public void onFeedDeleteClickedError() throws Exception {
        Feed feed = new Feed();
        Mockito.when(view.observeFeedOptionsClick()).thenReturn(Observable.just(feed));
        Mockito.when(view.observeFeedOptionMenuClick()).thenReturn(Observable.just(R.id.action_delete_feed));
        Mockito.when(model.deleteFeed(feed)).thenThrow(new Exception());

        presenter.onCreate();
        presenter.onResume();

        Mockito.verify(view).showFeedOptions();
        Mockito.verify(model).deleteFeed(feed);
        Mockito.verify(view).showError();
        Mockito.verify(view, Mockito.times(1)).showFeeds(mockFeedList);
    }

    @Test
    public void onMediaStateChangedTest() throws Exception {
        Mockito.when(model.observeMediaState()).thenReturn(Observable.just(
                MediaPlayerService.PLAYBACK_PLAYING,
                MediaPlayerService.PLAYBACK_PAUSED,
                MediaPlayerService.PLAYBACK_STOPPED,
                MediaPlayerService.PLAYBACK_PLAYING,
                MediaPlayerService.PLAYBACK_STOPPED
                ));

        presenter.onCreate();
        presenter.onDestroy();

        InOrder inOrder = Mockito.inOrder(view);
        // Initial load when stopped
        inOrder.verify(view).showMediaplayerBar(false);
        // Sequence of state changes
        inOrder.verify(view, Mockito.times(2)).showMediaplayerBar(true);
        inOrder.verify(view).showMediaplayerBar(false);
        inOrder.verify(view).showMediaplayerBar(true);
        inOrder.verify(view).showMediaplayerBar(false);
    }

    @Test
    public void onMediaStateInitialStoppedTest() throws Exception {
        Mockito.when(model.getInitialMediaState()).thenReturn(MediaPlayerService.PLAYBACK_STOPPED);

        presenter.onCreate();
        presenter.onDestroy();

        Mockito.verify(view).showMediaplayerBar(false);
    }

    @Test
    public void onMediaStateInitialPausedTest() throws Exception {
        Mockito.when(model.getInitialMediaState()).thenReturn(MediaPlayerService.PLAYBACK_PAUSED);

        presenter.onCreate();
        presenter.onDestroy();

        Mockito.verify(view).showMediaplayerBar(true);
    }

    @Test
    public void onMediaStateInitialPlayTest() throws Exception {
        Mockito.when(model.getInitialMediaState()).thenReturn(MediaPlayerService.PLAYBACK_PLAYING);

        presenter.onCreate();
        presenter.onDestroy();

        Mockito.verify(view).showMediaplayerBar(true);
    }
}
