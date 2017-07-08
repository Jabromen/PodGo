package me.bromen.podgo.app.mediaplayer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import me.bromen.podgo.BuildConfig;
import me.bromen.podgo.R;
import me.bromen.podgo.activities.mediacontrol.MediaControlActivity;
import me.bromen.podgo.extras.structures.AudioFile;

/**
 * Created by jeff on 6/30/17.
 */

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {

    private static boolean isRunning = false;

    public static boolean isRunning() {
        return isRunning;
    }

    private final IBinder iBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    // MediaPlayer Listeners

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopMedia();
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (BuildConfig.DEBUG) {
            switch (what) {
                case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                    Log.e("MediaPlayer Error", "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK " + extra);
                    break;
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    Log.e("MediaPlayer Error", "MEDIA_ERROR_SERVER_DIED " + extra);
                    break;
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                    Log.e("MediaPlayer Error", "MEDIA_ERROR_UNKNOWN " + extra);
                    break;
            }
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playMedia();
        durationObservable.onNext(getDuration());
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    // AudioFocus handling

    private AudioManager audioManager;

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mediaPlayer == null) {
                    initMediaPlayer();
                } else if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                pauseMedia();
                buildNotification(PLAYBACK_PAUSED);
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pauseMedia();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.setVolume(0.1f, 0.1f);
                }
                break;
        }
    }

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private boolean releaseAudioFocus() {
        if (audioManager == null) {
            return true;
        }
        int result = audioManager.abandonAudioFocus(this);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    // Audio Output Change Handling

    private final BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pauseMedia();
            buildNotification(PLAYBACK_PAUSED);
        }
    };

    private void registerBecomingNoisyReceiver() {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    // Incoming Phone Call Handling

    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    private void callStateListener() {
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                        }
                        ongoingCall = true;
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (currentAudio != null && ongoingCall) {
                            resumeMedia();
                        }
                        ongoingCall = false;
                        break;
                }
            }
        };
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    // Audio File Handling

    public static String PLAY_NEW_AUDIO = "me.bromen.podgo.action.PLAY_NEW_AUDIO";
    public static String QUEUE_NEW_AUDIO = "me.bromen.podgo.action.QUEUE_NEW_AUDIO";

    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                AudioFile audioFile = (AudioFile) intent.getSerializableExtra("AUDIOFILE");
                audioQueue.clear();
                audioQueue.add(audioFile);
            } catch (NullPointerException e) {
                e.printStackTrace();
                return;
            }

            stopMedia();
            if (mediaPlayer != null) {
                mediaPlayer.reset();
            }
            if (mediaSessionManager == null) {
                try {
                    initMediaSession();
                } catch (RemoteException e) {
                    e.printStackTrace();
                    stopSelf();
                }
            }
            initMediaPlayer();
            updateMetaData();
            handleIncomingActions(intent);
            buildNotification(PLAYBACK_PLAYING);
        }
    };

    private void registerPlayNewAudio() {
        IntentFilter intentFilter = new IntentFilter(MediaPlayerService.PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, intentFilter);
    }

    private BroadcastReceiver queueNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                AudioFile audioFile = (AudioFile) intent.getSerializableExtra("AUDIOFILE");
                audioQueue.add(audioFile);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    };

    private void registerQueueNewAudio() {
        IntentFilter intentFilter = new IntentFilter(MediaPlayerService.QUEUE_NEW_AUDIO);
        registerReceiver(queueNewAudio, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {

        AudioFile audioFile = null;
        try {
            audioFile = (AudioFile) intent.getExtras().getSerializable("AUDIOFILE");
        } catch (NullPointerException e) {

        }

        if (audioFile != null) {
            audioQueue.clear();
            audioQueue.add(audioFile);

            stopMedia();
            if (mediaPlayer != null) {
                mediaPlayer.reset();
            }
            try {
                initMediaSession();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
            initMediaPlayer();
            updateMetaData();
            buildNotification(PLAYBACK_PLAYING);
        }

        handleIncomingActions(intent);

        return super.onStartCommand(intent, flags, startId);
    }

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;

        callStateListener();
        registerBecomingNoisyReceiver();
        registerPlayNewAudio();
        registerQueueNewAudio();
        registerActionReceiver();

        stateObservable = PublishSubject.create();
        currentPositionObservable = PublishSubject.create();
        durationObservable = PublishSubject.create();
        currentAudioObservable = PublishSubject.create();

        disposables.add(Observable.interval(1, TimeUnit.SECONDS)
                .filter(__ -> getState() == PLAYBACK_PLAYING)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> currentPositionObservable.onNext(getCurrentPosition())));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isRunning = false;

        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }

        releaseAudioFocus();

        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        removeNotification();

        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(playNewAudio);
        unregisterReceiver(queueNewAudio);
        unregisterReceiver(actionReceiver);
        disposables.dispose();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        removeNotification();
        stopSelf();
    }

    private MediaPlayer mediaPlayer;
    private Queue<AudioFile> audioQueue = new ArrayDeque<>();
    private AudioFile currentAudio;

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);

        mediaPlayer.reset();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        currentAudio = audioQueue.poll();

        if (currentAudio == null) {
            stopSelf();
        }

        try {
            mediaPlayer.setDataSource(currentAudio.getAudioSource());
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            stopSelf();
        }
        mediaPlayer.prepareAsync();
    }

    private int resumePosition;

    private void playMedia() {
        if (requestAudioFocus()) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
            stateObservable.onNext(PLAYBACK_PLAYING);
            currentAudioObservable.onNext(currentAudio);
        }
    }

    private void stopMedia() {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            releaseAudioFocus();
            mediaPlayer.stop();
        }
        stateObservable.onNext(PLAYBACK_STOPPED);
    }

    private void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            releaseAudioFocus();
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
        stateObservable.onNext(getState());
        currentAudioObservable.onNext(currentAudio);
    }

    private void resumeMedia() {
        if (requestAudioFocus()) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.seekTo(resumePosition);
                mediaPlayer.start();
            }
            stateObservable.onNext(getState());
            currentAudioObservable.onNext(currentAudio);
        }
    }

    public static final String ACTION_PLAY = "me.bromen.podgo.action.PLAY";
    public static final String ACTION_PAUSE = "me.bromen.podgo.action.PAUSE";
    public static final String ACTION_PREVIOUS = "me.bromen.podgo.action.PREVIOUS";
    public static final String ACTION_NEXT = "me.bromen.podgo.action.NEXT";
    public static final String ACTION_SEEK_REL = "me.bromen.podgo.action.SEEK_REL";
    public static final String ACTION_SEEK_DIR = "me.bromen.podgo.action.SEEK_DIR";
    public static final String ACTION_STOP = "me.bromen.podgo.action.STOP";

    private BroadcastReceiver actionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleIncomingActions(intent);
        }
    };

    private void registerActionReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PLAY);
        intentFilter.addAction(ACTION_PAUSE);
        intentFilter.addAction(ACTION_PREVIOUS);
        intentFilter.addAction(ACTION_NEXT);
        intentFilter.addAction(ACTION_SEEK_REL);
        intentFilter.addAction(ACTION_SEEK_DIR);
        intentFilter.addAction(ACTION_STOP);

        registerReceiver(actionReceiver, intentFilter);
    }

    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat mediaController;
    private MediaControllerCompat.TransportControls transportControls;

    private static final int NOTIFICATION_ID = 1337;

    private void initMediaSession() throws RemoteException {
        if (mediaSessionManager != null) {
            return;
        }

        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        mediaSession = new MediaSessionCompat(getApplicationContext(), "PodGo");

        mediaSession.setSessionActivity(PendingIntent
                .getActivity(this, 0, new Intent(this, MediaControlActivity.class), 0));

        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);


        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
                buildNotification(PLAYBACK_PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();
                buildNotification(PLAYBACK_PAUSED);
            }

            @Override
            public void onStop() {
                super.onStop();
                stopMedia();
                removeNotification();
                stopSelf();
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
                mediaPlayer.seekTo((int) pos);
            }
        });

        mediaController = mediaSession.getController();
        transportControls = mediaController.getTransportControls();
        updateMetaData();
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            largeIcon = bitmap;
            buildNotification(lastPlaybackStatus);
        }
        @Override
        public void onBitmapFailed(Drawable errorDrawable) {}
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {}
    };

    private void updateMetaData() {
        if (currentAudio == null) {
            return;
        }

        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, currentAudio.getEpisodeTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, currentAudio.getPodcastTitle())
                .build());

        Picasso.with(this)
                .load(currentAudio.getImageUrl())
                .resize(150, 150)
                .centerCrop()
                .into(target);
    }

    public static final int PLAYBACK_PLAYING = 0;
    public static final int PLAYBACK_PAUSED = 1;
    public static final int PLAYBACK_STOPPED = 2;
    private static final int PLAYBACK_SEEK_BACK = 3;
    private static final int PLAYBACK_SEEK_FORWARD = 4;

    private Bitmap largeIcon;
    private int lastPlaybackStatus;

    private void buildNotification(int playbackStatus) {

        lastPlaybackStatus = playbackStatus;

        int playPauseIcon, smallIcon;
        String actionText;
        PendingIntent playPauseAction;

        if (playbackStatus == PLAYBACK_PLAYING) {
            playPauseIcon = R.drawable.pause_icon;
            smallIcon = R.drawable.play_icon;
            actionText = getString(R.string.pause);
            playPauseAction = playBackAction(PLAYBACK_PAUSED);
        } else if (playbackStatus == PLAYBACK_PAUSED) {
            playPauseIcon = R.drawable.play_icon;
            smallIcon = R.drawable.pause_icon;
            actionText = getString(R.string.play);
            playPauseAction = playBackAction(PLAYBACK_PLAYING);
        } else {
            playPauseIcon = R.drawable.pause_icon;
            smallIcon = R.drawable.play_icon;
            actionText = "Not Set";
            playPauseAction = null;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder
                // Set Text and Icons
                .setContentTitle(mediaController.getMetadata()
                        .getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE))
                .setContentText(mediaController.getMetadata()
                        .getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE))
                .setLargeIcon(largeIcon != null ?
                        largeIcon :
                        BitmapFactory.decodeResource(getResources(), R.drawable.ic_placeholder))
                // Set Notification click action
                .setContentIntent(mediaController.getSessionActivity())
                // Cancellable if paused, not if playing
                .setOngoing(playbackStatus == PLAYBACK_PLAYING)
                // Set Notification swipe action
                .setDeleteIntent(playBackAction(PLAYBACK_STOPPED))
                // Make visible on lock screen
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                // Set small icon image and color
                .setSmallIcon(smallIcon)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                // Add action(s)
                .addAction(R.drawable.seek_back_icon, getString(R.string.seek_back), playBackAction(PLAYBACK_SEEK_BACK))
                .addAction(playPauseIcon, actionText, playPauseAction)
                .addAction(R.drawable.seek_forward_icon, getString(R.string.seek_forward), playBackAction(PLAYBACK_SEEK_FORWARD))
                // Set layout style
                .setStyle(new NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(playBackAction(PLAYBACK_STOPPED)));

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(NOTIFICATION_ID, builder.build());
    }

    private void removeNotification() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIFICATION_ID);
    }

    private PendingIntent playBackAction(int actionType) {
        Intent playbackAction = new Intent(this, MediaPlayerService.class);

        switch (actionType) {
            case PLAYBACK_PLAYING:
                playbackAction.setAction(ACTION_PLAY);
                break;
            case PLAYBACK_PAUSED:
                playbackAction.setAction(ACTION_PAUSE);
                break;
            case PLAYBACK_STOPPED:
                playbackAction.setAction(ACTION_STOP);
                break;
            case PLAYBACK_SEEK_BACK:
                playbackAction.putExtra("SEEKTO", -30000);
                playbackAction.setAction(ACTION_SEEK_REL);
                break;
            case PLAYBACK_SEEK_FORWARD:
                playbackAction.putExtra("SEEKTO", 30000);
                playbackAction.setAction(ACTION_SEEK_REL);
                break;

            default:
                return null;
        }

        return PendingIntent.getService(this, actionType, playbackAction, 0);
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) {
            return;
        }

        String actionString = playbackAction.getAction();

        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
            stopForeground(true);
            stopSelf();
        } else if (actionString.equalsIgnoreCase(ACTION_SEEK_REL)) {
            // Seek to a position relative to current position
            int seekTo = playbackAction.getIntExtra("SEEKTO", 0);
            transportControls.seekTo(seekRelativePosition(seekTo));
        } else if (actionString.equalsIgnoreCase(ACTION_SEEK_DIR)) {
            // Seek directly to specified position
            int seekTo = playbackAction.getIntExtra("SEEKTO", 0);
            transportControls.seekTo(seekDirectPosition(seekTo));
        }
    }

    // Compute the new position relative to the current position
    private int seekRelativePosition(int seek) {
        return limitPosition(getCurrentPosition() + seek, 0, getDuration());
    }

    // Compute the new position directly
    private int seekDirectPosition(int seekTo) {
        return limitPosition(seekTo, 0, getDuration());
    }

    // Force the position to be within the specified limits
    private int limitPosition(int position, int lower, int upper) {
        if (position < lower) {
            return lower;
        } else if (position > upper) {
            return upper;
        } else {
            return position;
        }
    }

    // Getters for current media information

    private PublishSubject<Integer> stateObservable = PublishSubject.create();
    private PublishSubject<Integer> currentPositionObservable = PublishSubject.create();
    private PublishSubject<Integer> durationObservable = PublishSubject.create();
    private PublishSubject<AudioFile> currentAudioObservable = PublishSubject.create();

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
        return currentAudioObservable;
    }

    public int getState() {
        if (mediaPlayer == null) {
            return PLAYBACK_STOPPED;
        } else if (!mediaPlayer.isPlaying()) {
            return PLAYBACK_PAUSED;
        } else {
            return PLAYBACK_PLAYING;
        }
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    public AudioFile getCurrentAudio() {
        return currentAudio;
    }
}
