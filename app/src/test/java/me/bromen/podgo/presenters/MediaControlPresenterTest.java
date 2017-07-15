package me.bromen.podgo.presenters;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import io.reactivex.Observable;
import me.bromen.podgo.TrampolineSchedulerRule;
import me.bromen.podgo.activities.mediacontrol.mvp.MediaControlPresenter;
import me.bromen.podgo.activities.mediacontrol.mvp.contracts.MediaControlModel;
import me.bromen.podgo.activities.mediacontrol.mvp.contracts.MediaControlView;
import me.bromen.podgo.app.mediaplayer.MediaPlayerService;
import me.bromen.podgo.extras.structures.AudioFile;

/**
 * Created by jeff on 7/8/17.
 */

public class MediaControlPresenterTest {

    private MediaControlPresenter presenter;
    private MediaControlView view;
    private MediaControlModel model;

    private final int statePlaying = MediaPlayerService.PLAYBACK_PLAYING;
    private final int statePaused = MediaPlayerService.PLAYBACK_PAUSED;
    private final int stateStopped = MediaPlayerService.PLAYBACK_STOPPED;

    private final int position1 = 0;
    private final int position2 = 1000;
    private final int position3 = 2000;
    private final int position4 = 3000;

    private final int duration = 100000;
    private final int duration2 = 200000;

    private final AudioFile audioFile = new AudioFile();
    private final AudioFile audioFile2 = new AudioFile();

    @Rule
    public TrampolineSchedulerRule trampolineSchedulerRule = new TrampolineSchedulerRule();

    @Before
    public void setUp() throws Exception {
        view = Mockito.mock(MediaControlView.class);
        model = Mockito.mock(MediaControlModel.class);

        presenter = new MediaControlPresenter(view, model);

        Mockito.when(view.observePlayPause()).thenReturn(Observable.never());
        Mockito.when(view.observeSkipForward()).thenReturn(Observable.never());
        Mockito.when(view.observeSkipBackward()).thenReturn(Observable.never());
        Mockito.when(view.observeSeekbar()).thenReturn(Observable.never());

        Mockito.when(model.observeState()).thenReturn(Observable.never());
        Mockito.when(model.observePosition()).thenReturn(Observable.never());
        Mockito.when(model.observeDuration()).thenReturn(Observable.never());
        Mockito.when(model.observeAudioFile()).thenReturn(Observable.never());
    }

    @Test
    public void initialSetUpTest() throws Exception {
        presenter.onCreate();
        presenter.onDestroy();

        Mockito.verify(view).showControls(Mockito.anyBoolean());
        Mockito.verify(view).setState(Mockito.anyInt());
        Mockito.verify(view).setPosition(Mockito.anyInt());
        Mockito.verify(view).setDuration(Mockito.anyInt());
        Mockito.verify(view).setAudioFile(Mockito.any());
    }

    @Test
    public void initialStoppedTest() throws Exception {
        Mockito.when(model.getInitialState()).thenReturn(stateStopped);

        presenter.onCreate();
        presenter.onDestroy();

        Mockito.verify(view).showControls(false);
        Mockito.verify(view).setState(stateStopped);
        Mockito.verify(view).setPosition(Mockito.anyInt());
        Mockito.verify(view).setDuration(Mockito.anyInt());
        Mockito.verify(view).setAudioFile(Mockito.any());
    }

    @Test
    public void initialPlayingTest() throws Exception {
        Mockito.when(model.getInitialState()).thenReturn(statePlaying);
        Mockito.when(model.getInitialPosition()).thenReturn(position1);
        Mockito.when(model.getInitialDuration()).thenReturn(duration);
        Mockito.when(model.getInitialAudioFile()).thenReturn(audioFile);

        presenter.onCreate();
        presenter.onDestroy();

        Mockito.verify(view).showControls(true);
        Mockito.verify(view).setState(statePlaying);
        Mockito.verify(view).setPosition(position1);
        Mockito.verify(view).setDuration(duration);
        Mockito.verify(view).setAudioFile(audioFile);
    }

    @Test
    public void initialPausedTest() throws Exception {
        Mockito.when(model.getInitialState()).thenReturn(statePaused);
        Mockito.when(model.getInitialPosition()).thenReturn(position1);
        Mockito.when(model.getInitialDuration()).thenReturn(duration);
        Mockito.when(model.getInitialAudioFile()).thenReturn(audioFile);

        presenter.onCreate();
        presenter.onDestroy();

        Mockito.verify(view).showControls(true);
        Mockito.verify(view).setState(statePaused);
        Mockito.verify(view).setPosition(position1);
        Mockito.verify(view).setDuration(duration);
        Mockito.verify(view).setAudioFile(audioFile);
    }

    @Test
    public void stateChangeShowControlsTest() throws Exception {
        Mockito.when(model.getInitialState()).thenReturn(stateStopped);
        Mockito.when(model.observeState()).thenReturn(Observable.just(
                statePlaying,
                statePaused,
                statePlaying,
                stateStopped));

        presenter.onCreate();
        presenter.onDestroy();

        InOrder inOrder = Mockito.inOrder(view);
        // Initial state
        inOrder.verify(view).showControls(false);
        // Changing states
        inOrder.verify(view).showControls(true);
        inOrder.verify(view).showControls(true);
        inOrder.verify(view).showControls(true);
        inOrder.verify(view).showControls(false);
    }

    @Test
    public void stateChangeSetStateTest() throws Exception {
        Mockito.when(model.getInitialState()).thenReturn(stateStopped);
        Mockito.when(model.observeState()).thenReturn(Observable.just(
                statePlaying,
                statePaused,
                statePlaying,
                stateStopped));

        presenter.onCreate();
        presenter.onDestroy();

        InOrder inOrder = Mockito.inOrder(view);
        // Initial state
        inOrder.verify(view).setState(stateStopped);
        // Changing states
        inOrder.verify(view).setState(statePlaying);
        inOrder.verify(view).setState(statePaused);
        inOrder.verify(view).setState(statePlaying);
        inOrder.verify(view).setState(stateStopped);
    }

    @Test
    public void positionChangeTest() throws Exception {
        Mockito.when(model.getInitialPosition()).thenReturn(position1);
        Mockito.when(model.observePosition()).thenReturn(Observable.just(position2, position3, position4));

        presenter.onCreate();
        presenter.onDestroy();

        InOrder inOrder = Mockito.inOrder(view);
        // Initial position
        inOrder.verify(view).setPosition(position1);
        // Changing positions
        inOrder.verify(view).setPosition(position2);
        inOrder.verify(view).setPosition(position3);
        inOrder.verify(view).setPosition(position4);
    }

    @Test
    public void durationChangeTest() throws Exception {
        Mockito.when(model.getInitialDuration()).thenReturn(duration);
        Mockito.when(model.observeDuration()).thenReturn(Observable.just(duration2, duration, duration2));

        presenter.onCreate();
        presenter.onDestroy();

        InOrder inOrder = Mockito.inOrder(view);
        // Initial duration
        inOrder.verify(view).setDuration(duration);
        // Changing durations
        inOrder.verify(view).setDuration(duration2);
        inOrder.verify(view).setDuration(duration);
        inOrder.verify(view).setDuration(duration2);
    }

    @Test
    public void audioFileChangeTest() throws Exception {
        Mockito.when(model.getInitialAudioFile()).thenReturn(audioFile);
        Mockito.when(model.observeAudioFile()).thenReturn(Observable.just(audioFile2, audioFile, audioFile2));

        presenter.onCreate();
        presenter.onDestroy();

        InOrder inOrder = Mockito.inOrder(view);
        // Initial audiofile
        inOrder.verify(view).setAudioFile(audioFile);
        /// Changing audiofiles
        inOrder.verify(view).setAudioFile(audioFile2);
        inOrder.verify(view).setAudioFile(audioFile);
        inOrder.verify(view).setAudioFile(audioFile2);
    }

    @Test
    public void playPauseClickedTest() throws Exception {
        Mockito.when(view.observePlayPause()).thenReturn(Observable.just(new Object()));

        presenter.onCreate();
        presenter.onDestroy();

        Mockito.verify(model).playPause();
    }

    @Test
    public void skipForwardClickedTest() throws Exception {
        Mockito.when(view.observeSkipForward()).thenReturn(Observable.just(new Object()));

        presenter.onCreate();
        presenter.onDestroy();

        Mockito.verify(model).seekForward();
    }

    @Test
    public void skipBackwardClickedTest() throws Exception {
        Mockito.when(view.observeSkipBackward()).thenReturn(Observable.just(new Object()));

        presenter.onCreate();
        presenter.onDestroy();

        Mockito.verify(model).seekBackward();
    }

    @Test
    public void seekbarClickedTest() throws Exception {
        Mockito.when(view.observeSeekbar()).thenReturn(Observable.just(position1, position2, position3, position4));

        presenter.onCreate();
        presenter.onDestroy();

        InOrder inOrder = Mockito.inOrder(model);
        inOrder.verify(model).seekDirect(position1);
        inOrder.verify(model).seekDirect(position2);
        inOrder.verify(model).seekDirect(position3);
        inOrder.verify(model).seekDirect(position4);
    }
}
