package me.bromen.podgo.app.parser;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import me.bromen.podgo.ext.structures.Feed;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jeff on 6/20/17.
 */

public class FeedParser {

    private final OkHttpClient okHttpClient;

    public FeedParser(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public Feed parseFeedFromUrl(String feedUrl) throws Exception {
        Request request = new Request.Builder().url(feedUrl).build();

        try {
            Response response = okHttpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                InputStream in = response.body().byteStream();

                Feed feed = parseFeedFromStream(in);
                in.close();

                return feed;
            } else {
                throw new Exception("HTTP Response Failed");
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("IOException");
        } catch (SAXException e) {
            e.printStackTrace();
            throw new Exception("SAXException");
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
