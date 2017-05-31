package me.bromen.podgo.downloads;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EmptyStackException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import me.bromen.podgo.parser.FeedHandler;
import me.bromen.podgo.structures.Feed;

/**
 * Created by jeff on 5/17/17.
 */

public class FeedRequestService extends IntentService {

    public FeedRequestService() {
        super("FeedRequestService");
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

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            FeedHandler feedHandler = new FeedHandler();
            saxParser.parse(in, feedHandler);

            Feed feed = feedHandler.getFeed();
            feed.setFeedUrl(feedUrl);
            feed.setItemPlaces();

            Bundle bundle = new Bundle();
            bundle.putSerializable("FEED", feed);
            bundle.putBoolean("REFRESH", refresh);

            receiver.send(FeedResultReceiver.SUCCESS, bundle);

        } catch (IOException | SAXException | ParserConfigurationException | EmptyStackException e) {
            e.printStackTrace();
            receiver.send(FeedResultReceiver.FAILURE, null);
        }
    }
}
