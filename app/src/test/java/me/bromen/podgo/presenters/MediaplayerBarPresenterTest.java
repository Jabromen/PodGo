package me.bromen.podgo.presenters;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import io.reactivex.Observable;
import me.bromen.podgo.TrampolineSchedulerRule;
import me.bromen.podgo.app.mediaplayer.MediaPlayerService;
import me.bromen.podgo.extras.structures.AudioFile;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.MediaplayerBarPresenter;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.contracts.MediaplayerBarModel;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.contracts.MediaplayerBarView;

/**
 * Created by Jeffrey on 7/7/2017.
 */

public class MediaplayerBarPresenterTest {

    private MediaplayerBarPresenter presenter;
    private MediaplayerBarView view;
    private MediaplayerBarModel model;

    private final AudioFile audioFile = new AudioFile();
    private final AudioFile audioFile2 = new AudioFile();
    private final AudioFile audioFile3 = new AudioFile();

    @Rule
    public TrampolineSchedulerRule trampolineSchedulerRule = new TrampolineSchedulerRule();

    @Before
    public void setUp() {
        view = Mockito.mock(MediaplayerBarView.class);
        model = Mockito.mock(MediaplayerBarModel.class);

        presenter = new MediaplayerBarPresenter(view, model);

        Mockito.when(view.observeBarClick()).thenReturn(Observable.never());
        Mockito.when(view.observePlayPauseClick()).thenReturn(Observable.never());
        Mockito.when(model.observeState()).thenReturn(Observable.never());
        Mockito.when(model.observeAudioFile()).thenReturn(Observable.never());
        Mockito.when(model.getInitialState()).thenReturn(MediaPlayerService.PLAYBACK_STOPPED);
        Mockito.when(model.getInitialAudioFile()).thenReturn(audioFile);
    }

    @Test
    public void initialSetupTest() throws Exception {
        presenter.onCreate();
        presenter.onDestroy();

        Mockito.verify(view).setState(MediaPlayerService.PLAYBACK_STOPPED);
        Mockito.verify(view).setAudioFile(audioFile);
        Mockito.verify(model, Mockito.never()).clickBar();
        Mockito.verify(model, Mockito.never()).playPause();
    }

    @Test
    public void initialStateStoppedTest() throws Exception {
        Mockito.when(model.getInitialState()).thenReturn(MediaPlayerService.PLAYBACK_STOPPED);

        presenter.onCreate();
        presenter.onDestroy();

        Mockito.verify(view).setState(MediaPlayerService.PLAYBACK_STOPPED);
    }

    @Test
    public void initialStatePlayingTest() throws Exception {
        Mockito.when(model.getInitialState()).thenReturn(MediaPlayerService.PLAYBACK_PLAYING);

        presenter.onCreate();
        presenter.onDestroy();

        Mockito.verify(view).setState(MediaPlayerService.PLAYBACK_PLAYING);
    }

    @Test
    public void initialStatePausedTest() throws Exception {
        Mockito.when(model.getInitialState()).thenReturn(MediaPlayerService.PLAYBACK_PAUSED);

        presenter.onCreate();
        presenter.onDestroy();

        Mockito.verify(view).setState(MediaPlayerService.PLAYBACK_PAUSED);
    }

    @Test
    public void mediaStateChangeTest() throws Exception {
        Mockito.when(model.observeState()).thenReturn(Observable.just(
                MediaPlayerService.PLAYBACK_PLAYING,
                MediaPlayerService.PLAYBACK_PAUSED,
                MediaPlayerService.PLAYBACK_PLAYING,
                MediaPlayerService.PLAYBACK_PAUSED,
                MediaPlayerService.PLAYBACK_STOPPED
        ));

        presenter.onCreate();
        presenter.onDestroy();

        InOrder inOrder = Mockito.inOrder(view);
        // Initial setup when stopped
        inOrder.verify(view).setState(MediaPlayerService.PLAYBACK_STOPPED);
        // Sequence of state changes
        inOrder.verify(view).setState(MediaPlayerService.PLAYBACK_PLAYING);
        inOrder.verify(view).setState(MediaPlayerService.PLAYBACK_PAUSED);
        inOrder.verify(view).setState(MediaPlayerService.PLAYBACK_PLAYING);
        inOrder.verify(view).setState(MediaPlayerService.PLAYBACK_PAUSED);
        inOrder.verify(view).setState(MediaPlayerService.PLAYBACK_STOPPED);
    }

    @Test
    public void audioFileChangeTest() throws Exception {
        Mockito.when(model.getInitialAudioFile()).thenReturn(null);
        Mockito.when(model.observeAudioFile()).thenReturn(Observable.just(audioFile, audioFile2, audioFile3));

        presenter.onCreate();
        presenter.onDestroy();

        InOrder inOrder = Mockito.inOrder(view);
        // Initial null audio file
        inOrder.verify(view).setAudioFile(null);
        // Sequence of audio files
        inOrder.verify(view).setAudioFile(audioFile);
        inOrder.verify(view).setAudioFile(audioFile2);
        inOrder.verify(view).setAudioFile(audioFile3);
    }

    @Test
    public void mediaplayerBarClickTest() throws Exception {
        Mockito.when(view.observeBarClick()).thenReturn(Observable.just(new Object()));

        presenter.onCreate();
        presenter.onDestroy();

        Mockito.verify(model).clickBar();
    }

    @Test
    public void mediaplayerPlayPauseClickTest() throws Exception {
        Mockito.when(view.observePlayPauseClick()).thenReturn(Observable.just(new Object()));

        presenter.onCreate();
        presenter.onDestroy();

        Mockito.verify(model).playPause();
    }
}
