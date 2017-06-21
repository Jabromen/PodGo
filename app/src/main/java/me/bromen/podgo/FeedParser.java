package me.bromen.podgo;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import me.bromen.podgo.parser.FeedHandler;
import me.bromen.podgo.structures.Feed;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import rx.Single;

/**
 * Created by jeff on 6/20/17.
 */

public class FeedParser {

    private final OkHttpClient okHttpClient;

    public FeedParser(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public Single<Feed> parseFeedFromUrl(String feedUrl) throws Throwable {
        Request request = new Request.Builder().url(feedUrl).build();

        try {
            Response response = okHttpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                InputStream in = response.body().byteStream();

                Feed feed = parseFeedFromStream(in);
                in.close();

                return Single.just(feed);
            } else {
                throw new Throwable("HTTP Response Failed");
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new Throwable("IOException");
        } catch (SAXException e) {
            e.printStackTrace();
            throw new Throwable("SAXException");
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
