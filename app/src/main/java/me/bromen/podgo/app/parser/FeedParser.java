package me.bromen.podgo.app.parser;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import me.bromen.podgo.app.storage.DbHelper;
import me.bromen.podgo.extras.structures.Feed;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jeff on 6/20/17.
 */

public class FeedParser {

    private final OkHttpClient okHttpClient;
    private final DbHelper dbHelper;
    private final DateTimeFormatter dateTimeFormatter;

    public FeedParser(OkHttpClient okHttpClient, DbHelper dbHelper) {
        this.okHttpClient = okHttpClient;
        this.dbHelper = dbHelper;

        // TODO: Make sure all valid formats are covered
        DateTimeParser[] parsers = {
                DateTimeFormat.forPattern("EEE, d MMM yyyy HH:mm:ss z").getParser(),
                DateTimeFormat.forPattern("EEE, d MMM yyyy HH:mm:ss Z").getParser(),
                DateTimeFormat.forPattern("EEE, d MMM yyyy HH:mm:ss ZZ").getParser(),
                DateTimeFormat.forPattern("EEE, d MMM yyyy HH:mm z").getParser(),
                DateTimeFormat.forPattern("EEE, d MMM yyyy HH:mm Z").getParser(),
                DateTimeFormat.forPattern("EEE, d MMM yyyy HH:mm ZZ").getParser(),
        };
        this.dateTimeFormatter = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();
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

                feed.setFeedUrl(feedUrl);
                if (feed.getFeedItems().size() > 0) {
                    feed.setRecentEnclosureUrl(feed.getFeedItems().get(0).getEnclosure().getUrl());
                }

                return dbHelper.saveFeed(feed) ? "" : "Already Saved";
            } else {
                return "Http Response Failed";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public Integer refreshFeed(Feed feed) {
        String feedUrl = feed.getFeedUrl();
        try {
            if (!feedUrl.startsWith("http://") && !feedUrl.startsWith("https://")) {
                feedUrl = "http://" + feedUrl;
            }

            Request request = new Request.Builder().url(feedUrl).build();
            Response response = okHttpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                InputStream in = response.body().byteStream();

                Feed newFeed = parseFeedFromStream(in, feed.getRecentEnclosureUrl());
                in.close();

                newFeed.setFeedUrl(feedUrl);
                if (newFeed.getFeedItems().size() > 0) {
                    newFeed.setRecentEnclosureUrl(newFeed.getFeedItems().get(0).getEnclosure().getUrl());
                }

                return dbHelper.updateFeed(newFeed);
            } else {
                return -1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private Feed parseFeedFromStream(InputStream in) throws IOException, SAXException, ParserConfigurationException {
        SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        FeedHandler feedHandler = new FeedHandler(dateTimeFormatter);

        saxParser.parse(in, feedHandler);

        return feedHandler.getFeed();
    }

    private Feed parseFeedFromStream(InputStream in, String recentEnclosureUrl) throws IOException, SAXException, ParserConfigurationException {
        SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        FeedHandler feedHandler = new FeedHandler(dateTimeFormatter, recentEnclosureUrl);
        try {
            saxParser.parse(in, feedHandler);
        } catch (SaxEndParserEarlyException e) {
            // Do nothing, this exception is only thrown to break out of parsing early
        }
        return feedHandler.getFeed();
    }
}
