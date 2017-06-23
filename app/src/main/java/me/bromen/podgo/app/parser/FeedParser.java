package me.bromen.podgo.app.parser;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import me.bromen.podgo.app.storage.PodcastDbHelper;
import me.bromen.podgo.ext.structures.Feed;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jeff on 6/20/17.
 */

public class FeedParser {

    private final OkHttpClient okHttpClient;
    private final PodcastDbHelper dbHelper;

    public FeedParser(OkHttpClient okHttpClient, PodcastDbHelper dbHelper) {
        this.okHttpClient = okHttpClient;
        this.dbHelper = dbHelper;
    }

    public String parseFeedFromUrl(String feedUrl) {

        try {
            if (!feedUrl.startsWith("http://") && !feedUrl.startsWith("https://")) {
                feedUrl = "http://" + feedUrl;
            }

            Request request = new Request.Builder().url(feedUrl).build();
            Response response = okHttpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                InputStream in = response.body().byteStream();

                Feed feed = parseFeedFromStream(in);
                in.close();

                return dbHelper.saveFeed(feed) ? "" : "Already Saved";
            } else {
                return "Http Response Failed";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private Feed parseFeedFromStream(InputStream in) throws IOException, SAXException, ParserConfigurationException {
        SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        FeedHandler feedHandler = new FeedHandler();

        saxParser.parse(in, feedHandler);
        saxParser.reset();

        return feedHandler.getFeed();
    }
}
