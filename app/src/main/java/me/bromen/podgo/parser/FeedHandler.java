package me.bromen.podgo.parser;

import android.content.Context;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.EmptyStackException;
import java.util.Stack;

import me.bromen.podgo.structures.Feed;
import me.bromen.podgo.structures.FeedItem;
import me.bromen.podgo.structures.FeedItemEnclosure;

/**
 * Created by jeff on 5/31/17.
 */

public class FeedHandler extends DefaultHandler {

    private final String RSS = "rss";
    private final String CHANNEL = "channel";
    private final String ITEM = "item";
    private final String ITUNESIMAGE = "itunes:image";
    private final String ENCLOSURE = "enclosure";

    private final String TITLE = "title";
    private final String LINK = "link";
    private final String URL = "url";
    private final String PUBDATE = "pubDate";
    private final String DESCRIPTION = "description";
    private final String TYPE = "type";
    private final String LENGTH = "length";
    private final String HREF = "href";

    private boolean bTitle = false;
    private boolean bDescription = false;
    private boolean bLink = false;
    private boolean bPubDate = false;

    private Stack<String> typeStack = new Stack<>();

    private Feed feed;
    private FeedItem item;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException, EmptyStackException {

        if (RSS.equalsIgnoreCase(qName)) {
            if (typeStack.isEmpty()) {
                typeStack.add(RSS);
            }
            else {
                throw new SAXException("Invalid rss placement");
            }
        }
        else if (CHANNEL.equalsIgnoreCase(qName)) {
            if (RSS.equalsIgnoreCase(typeStack.peek())) {
                feed = new Feed();
                typeStack.add(CHANNEL);
            }
            else {
                throw new SAXException("Invalid channel placement");
            }
        }
        else if (ITEM.equalsIgnoreCase(qName)) {
            if (CHANNEL.equalsIgnoreCase(typeStack.peek())) {
                item = new FeedItem();
                typeStack.add(ITEM);
            }
            else {
                throw new SAXException("Invalid item placement");
            }
        }
        else if (ITUNESIMAGE.equalsIgnoreCase(qName)) {
            if (CHANNEL.equalsIgnoreCase(typeStack.peek())) {
                feed.setImageUrl(attributes.getValue(HREF));
            }
            else if (ITEM.equalsIgnoreCase(typeStack.peek())) {
                // TODO: Add functionality for individual episode images
            }
        }
        else if (TITLE.equalsIgnoreCase(qName)) {
            bTitle = true;
        }
        else if (DESCRIPTION.equalsIgnoreCase(qName)) {
            bDescription = true;
        }
        else if (LINK.equalsIgnoreCase(qName)) {
            bLink = true;
        }
        else if (PUBDATE.equalsIgnoreCase(qName)) {
            bPubDate = true;
        }
        else if (ENCLOSURE.equalsIgnoreCase(qName)) {
            if (ITEM.equalsIgnoreCase(typeStack.peek())) {
                String url = attributes.getValue(URL);
                String type = attributes.getType(TYPE);
                String length = attributes.getType(LENGTH);

                if (url == null || type == null) {
                    throw new SAXException("Missing enclosure attributes");
                }

                item.setEnclosure(new FeedItemEnclosure(url, type, length));
            }
            else {
                throw new SAXException("Invalid enclosure placement");
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException, EmptyStackException {
        if (RSS.equalsIgnoreCase(qName)) {
            typeStack.pop();
        }
        else if (CHANNEL.equalsIgnoreCase(qName)) {
            typeStack.pop();
        }
        else if (ITEM.equalsIgnoreCase(qName)) {
            feed.getFeedItems().add(item);
            typeStack.pop();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (bTitle) {
            if (CHANNEL.equalsIgnoreCase(typeStack.peek())) {
                feed.setTitle(new String(ch, start, length));
            }
            else if (ITEM.equalsIgnoreCase(typeStack.peek())) {
                item.setTitle(new String(ch, start, length));
            }
            else {
                throw new SAXException("Invalid title placement");
            }
            bTitle = false;
        }
        else if (bDescription) {
            if (CHANNEL.equalsIgnoreCase(typeStack.peek())) {
                feed.setDescription(new String(ch, start, length));
            }
            else if (ITEM.equalsIgnoreCase(typeStack.peek())) {
                item.setDescription(new String(ch, start, length));
            }
            else {
                throw new SAXException("Invalid description placement");
            }
            bDescription = false;
        }
        else if (bLink) {
            if (CHANNEL.equalsIgnoreCase(typeStack.peek())) {
                feed.setLink(new String(ch, start, length));
            }
            else if (ITEM.equalsIgnoreCase(typeStack.peek())) {
                item.setLink(new String(ch, start, length));
            }
            else {
                throw new SAXException("Invalid link placement");
            }
            bLink = false;
        }
        else if (bPubDate) {
            if (CHANNEL.equalsIgnoreCase(typeStack.peek())) {

            }
            else if (ITEM.equalsIgnoreCase(typeStack.peek())) {
                item.setPubDate(new String(ch, start, length));
            }
            else {
                throw new SAXException("Invalid pubDate placement");
            }
            bPubDate = false;
        }
    }

    public Feed getFeed() {
        return feed;
    }
}
