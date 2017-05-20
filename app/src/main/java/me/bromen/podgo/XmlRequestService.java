package me.bromen.podgo;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by jeff on 5/17/17.
 */

public class XmlRequestService extends IntentService {

    public XmlRequestService() {
        super("XmlRequestService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String feedUrl = intent.getStringExtra("URL");
        ResultReceiver receiver = intent.getParcelableExtra("RECEIVER");
        boolean refresh = intent.getBooleanExtra("REFRESH", false);

        try {
            URL url = new URL(feedUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String xmlFeed = IOUtils.toString(in, Charset.forName("UTF-8"));

            Bundle bundle = new Bundle();
            bundle.putString("URL", feedUrl);
            bundle.putString("XML", xmlFeed);
            bundle.putBoolean("REFRESH", refresh);

            receiver.send(XmlResultReceiver.SUCCESS, bundle);

        } catch (IOException e) {
            e.printStackTrace();
            receiver.send(XmlResultReceiver.FAILURE, null);
        }
    }
}
