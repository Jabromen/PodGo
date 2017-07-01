package me.bromen.podgo.app.mediaplayer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import me.bromen.podgo.BuildConfig;
import me.bromen.podgo.extras.structures.AudioFile;

/**
 * Created by jeff on 6/30/17.
 */

public class MediaPlayerServiceController {

    private final Context context;
    private final ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (BuildConfig.DEBUG) {
                Log.d("PodGo", "MediaPlayerService Connected");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (BuildConfig.DEBUG) {
                Log.d("PodGo", "MediaPlayerService Disconnected");
            }
        }
    };

    public MediaPlayerServiceController(Context context) {
        this.context = context;
    }

    public void bind() {
        Intent intent = new Intent(context, MediaPlayerService.class);
        context.bindService(intent, serviceConn, Context.BIND_AUTO_CREATE);
    }

    public void stop() {
        context.stopService(new Intent(context, MediaPlayerService.class));
        context.unbindService(serviceConn);
    }

    public void play(AudioFile audioFile) {
        Intent intent = new Intent(MediaPlayerService.PLAY_NEW_AUDIO);
        intent.putExtra("AUDIOFILE", audioFile);
        context.sendBroadcast(intent);
    }
}
