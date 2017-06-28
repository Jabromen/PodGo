package me.bromen.podgo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import io.reactivex.Observable;
import me.bromen.podgo.activities.feeddetail.mvp.FeedDetailPresenter;
import me.bromen.podgo.activities.feeddetail.mvp.contracts.FeedDetailModel;
import me.bromen.podgo.activities.feeddetail.mvp.contracts.FeedDetailView;
import me.bromen.podgo.extras.structures.Feed;
import me.bromen.podgo.extras.structures.FeedItem;

/**
 * Created by jeff on 6/22/17.
 */

public class FeedDetailPresenterTest {

    private FeedDetailPresenter presenter;
    private FeedDetailModel model;
    private FeedDetailView view;

    private final long id = 1;

    private final Feed feed = new Feed();
    private final FeedItem item = new FeedItem();

    @Rule
    public TrampolineSchedulerRule trampolineSchedulerRule = new TrampolineSchedulerRule();

    @Before
    public void setUp() throws Exception {
        view = Mockito.mock(FeedDetailView.class);
        model = Mockito.mock(FeedDetailModel.class);

        presenter = new FeedDetailPresenter(view, model, id);

        Mockito.when(view.observeItemTileClick()).thenReturn(Observable.never());
        Mockito.when(view.observeItemActionClick()).thenReturn(Observable.never());
        Mockito.when(model.observeDownloads()).thenReturn(Observable.never());
    }

    @Test
    public void onObserveLoadFeedSuccess() throws Exception {
        Mockito.when(model.loadFeed(id)).thenReturn(feed);

        presenter.onCreate();

        InOrder inOrder = Mockito.inOrder(view);
        inOrder.verify(view).showLoading(true);
        inOrder.verify(view).showLoading(false);
        inOrder.verify(view).showFeed(feed);
    }

    @Test
    public void onObserveLoadFeedError() throws Exception {
        Mockito.when(model.loadFeed(id)).thenThrow(new Exception());

        presenter.onCreate();

        InOrder inOrder = Mockito.inOrder(view);
        inOrder.verify(view).showLoading(true);
        inOrder.verify(view).showLoading(false);
        inOrder.verify(view).showError();
    }

    @Test
    public void onObserveItemTileClicked() throws Exception {
        Mockito.when(model.loadFeed(id)).thenReturn(feed);
        Mockito.when(view.observeItemTileClick()).thenReturn(Observable.just(item));

        presenter.onCreate();

        Mockito.verify(model).startFeedItemDetailActivity(item);
    }

    @Test
    public void onObserveItemActionClickedDownload() throws Exception {
        item.setDownloading(false);
        item.setDownloaded(false);
        Mockito.when(model.loadFeed(id)).thenReturn(feed);
        Mockito.when(view.observeItemActionClick()).thenReturn(Observable.just(item));

        presenter.onCreate();

        Mockito.verify(model).startDownload(item);
    }

    @Test
    public void onObserveItemActionClickedCancel() throws Exception {
        item.setDownloading(true);
        item.setDownloaded(true);
        Mockito.when(model.loadFeed(id)).thenReturn(feed);
        Mockito.when(view.observeItemActionClick()).thenReturn(Observable.just(item));

        presenter.onCreate();

        Mockito.verify(model).cancelDownload(item);
    }

    @Test
    public void onObserveItemActionClickedPlay() throws Exception {
        item.setDownloading(false);
        item.setDownloaded(true);
        Mockito.when(model.loadFeed(id)).thenReturn(feed);
        Mockito.when(view.observeItemActionClick()).thenReturn(Observable.just(item));

        presenter.onCreate();

        Mockito.verify(model).playEpisode(item);
    }

    @Test
    public void onSingleDownloadReceived() throws Exception {
        Mockito.when(model.loadFeed(id)).thenReturn(feed);
        Mockito.when(model.observeDownloads()).thenReturn(Observable.just(true));

        presenter.onCreate();

        // Called once for initial load, then again when download is received
        Mockito.verify(view, Mockito.times(2)).showFeed(feed);
    }

    @Test
    public void onMultipleDownloadReceived() throws Exception {
        Mockito.when(model.loadFeed(id)).thenReturn(feed);
        Mockito.when(model.observeDownloads()).thenReturn(Observable.just(true, true, true));

        presenter.onCreate();

        // Called once for initial load, then again for each of 3 downloads
        Mockito.verify(view, Mockito.times(4)).showFeed(feed);
    }
}
