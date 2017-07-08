package me.bromen.podgo.app.mediaplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;
import me.bromen.podgo.BuildConfig;
import me.bromen.podgo.extras.structures.AudioFile;

/**
 * Created by jeff on 6/30/17.
 */

public class MediaPlayerServiceController {

    private final Context context;

    private MediaPlayerService player;

    private CompositeDisposable disposables;

    private int state = MediaPlayerService.PLAYBACK_STOPPED;
    private int currentPosition;
    private int duration;
    private AudioFile audioFile;

    private final PublishSubject<Integer> stateObservable = PublishSubject.create();
    private final PublishSubject<Integer> currentPositionObservable = PublishSubject.create();
    private final PublishSubject<Integer> durationObservable = PublishSubject.create();
    private final PublishSubject<AudioFile> audioFileObservable = PublishSubject.create();

    private void initObservables() {
        disposables = new CompositeDisposable();
        disposables.add(player.observeState().subscribe(state -> {
            this.state = state;
            stateObservable.onNext(state);
        }));
        disposables.add(player.observeCurrentPosition().subscribe(currentPosition -> {
            this.currentPosition = currentPosition;
            currentPositionObservable.onNext(currentPosition);
        }));
        disposables.add(player.observeDuration().subscribe(duration -> {
            this.duration = duration;
            durationObservable.onNext(duration);
        }));
        disposables.add(player.observeAudioFile().subscribe(audioFile -> {
            this.audioFile = audioFile;
            audioFileObservable.onNext(audioFile);
        }));

        state = player.getState();
        stateObservable.onNext(player.getState());
    }

    private final ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (BuildConfig.DEBUG) {
                Log.d("PodGo", "MediaPlayerService Connected");
            }
            player = ((MediaPlayerService.LocalBinder) service).getService();
            initObservables();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (BuildConfig.DEBUG) {
                Log.d("PodGo", "MediaPlayerService Disconnected");
            }
            player = null;
            disposables.dispose();
            disposables = null;
        }
    };

    public MediaPlayerServiceController(Context context) {
        this.context = context;
    }

    // Start/Stop Service

    public void bindService() {
        Intent intent = new Intent(context, MediaPlayerService.class);
        startService();
        context.bindService(intent, serviceConn, 0);
    }

    public void unbindService() {
        context.unbindService(serviceConn);
    }

    public void startService() {
        if (!MediaPlayerService.isRunning) {
            Intent intent = new Intent(context, MediaPlayerService.class);
            context.startService(intent);
        }
    }

    public void stopService() {
        Intent intent = new Intent(context, MediaPlayerService.class);
        context.stopService(intent);
    }

    // Broadcast Intent Methods

    public void play(AudioFile audioFile) {
        if (!MediaPlayerService.isRunning) {
            Intent intent = new Intent(context, MediaPlayerService.class);
            intent.putExtra("AUDIOFILE", audioFile);
            context.startService(intent);
            bindService();
        } else {
            Intent intent = new Intent(MediaPlayerService.PLAY_NEW_AUDIO);
            intent.putExtra("AUDIOFILE", audioFile);
            context.sendBroadcast(intent);
        }
    }

    public void queue(AudioFile audioFile) {
        Intent intent = new Intent(MediaPlayerService.QUEUE_NEW_AUDIO);
        intent.putExtra("AUDIOFILE", audioFile);
        context.sendBroadcast(intent);
    }

    public void playPause() {
        if (state == MediaPlayerService.PLAYBACK_PLAYING) {
            Intent intent = new Intent(MediaPlayerService.ACTION_PAUSE);
            context.sendBroadcast(intent);
        } else if (state == MediaPlayerService.PLAYBACK_PAUSED) {
            Intent intent = new Intent(MediaPlayerService.ACTION_PLAY);
            context.sendBroadcast(intent);
        }
    }

    public void stop() {
        Intent intent = new Intent(MediaPlayerService.ACTION_STOP);
        context.sendBroadcast(intent);
    }

    public void seekRelative(int seekTo) {
        Intent intent = new Intent(MediaPlayerService.ACTION_SEEK_REL);
        intent.putExtra("SEEKTO", seekTo);
        context.sendBroadcast(intent);
    }

    public void seekDirect(int seekTo) {
        Intent intent = new Intent(MediaPlayerService.ACTION_SEEK_DIR);
        intent.putExtra("SEEKTO", seekTo);
        context.sendBroadcast(intent);
    }

    // Getters for current media information

    public int getState() {
        return state;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public int getDuration() {
        return duration;
    }

    public AudioFile getCurrentAudio() {
        return audioFile;
    }

    // Getters for media observables

    public Observable<Integer> observeState() {
        return stateObservable;
    }

    public Observable<Integer> observeCurrentPosition() {
        return currentPositionObservable;
    }

    public Observable<Integer> observeDuration() {
        return durationObservable;
    }

    public Observable<AudioFile> observeAudioFile() {
        return audioFileObservable;
    }
}
