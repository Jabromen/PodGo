package me.bromen.podgo.downloads;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by jeff on 5/17/17.
 */

public class FeedResultReceiver extends ResultReceiver {

    public static final int SUCCESS = 0;
    public static final int FAILURE = 1;

    private Receiver mReceiver;

    public FeedResultReceiver(Handler handler) {
        super(handler);
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }

    }
}
