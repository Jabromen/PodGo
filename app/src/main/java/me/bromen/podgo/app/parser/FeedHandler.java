package me.bromen.podgo.app.parser;

import org.joda.time.format.DateTimeFormatter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.EmptyStackException;
import java.util.Locale;
import java.util.Stack;

import me.bromen.podgo.extras.structures.Feed;
import me.bromen.podgo.extras.structures.FeedItem;
import me.bromen.podgo.extras.structures.FeedItemEnclosure;

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

    private boolean bCharacters;

    private boolean bRefresh;
    private String recentEnclosureUrl;

    private Stack<String> tagStack = new Stack<>();

    private Feed feed;
    private FeedItem item;

    private final DateTimeFormatter dateTimeFormatter;
    private final Locale locale = new Locale("en_US");
    private final StringBuilder stringBuilder = new StringBuilder();

    public FeedHandler(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    public FeedHandler(DateTimeFormatter dateTimeFormatter, String recentEnclosureUrl) {
        this.dateTimeFormatter = dateTimeFormatter;
        this.recentEnclosureUrl = recentEnclosureUrl;
        bRefresh = true;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException, EmptyStackException {

        if (RSS.equalsIgnoreCase(qName)) {
            if (tagStack.isEmpty()) {
                tagStack.add(RSS);
            } else {
                throw new SAXException("Invalid rss placement");
            }
        } else if (CHANNEL.equalsIgnoreCase(qName)) {
            if (RSS.equalsIgnoreCase(tagStack.peek())) {
                feed = new Feed();
                tagStack.add(CHANNEL);
            } else {
                throw new SAXException("Invalid channel placement");
            }
        } else if (ITEM.equalsIgnoreCase(qName)) {
            if (CHANNEL.equalsIgnoreCase(tagStack.peek())) {
                item = new FeedItem();
                tagStack.add(ITEM);
            } else {
                throw new SAXException("Invalid item placement");
            }
        } else if (ITUNESIMAGE.equalsIgnoreCase(qName)) {
            if (CHANNEL.equalsIgnoreCase(tagStack.peek())) {
                feed.setImageUrl(attributes.getValue(HREF));
            } else if (ITEM.equalsIgnoreCase(tagStack.peek())) {
                item.setImageUrl(attributes.getValue(HREF));
            }
        } else if (ENCLOSURE.equalsIgnoreCase(qName)) {
            if (ITEM.equalsIgnoreCase(tagStack.peek())) {
                String url = attributes.getValue(URL);
                String type = attributes.getType(TYPE);
                String length = attributes.getType(LENGTH);

                if (url == null || type == null) {
                    throw new SAXException("Missing enclosure attributes");
                }

                item.setEnclosure(new FeedItemEnclosure(url, type, length));
            } else {
                throw new SAXException("Invalid enclosure placement");
            }
        } else if (TITLE.equalsIgnoreCase(qName)) {
            bCharacters = true;
        } else if (DESCRIPTION.equalsIgnoreCase(qName)) {
            bCharacters = true;
        } else if (LINK.equalsIgnoreCase(qName)) {
            bCharacters = true;
        } else if (PUBDATE.equalsIgnoreCase(qName)) {
            bCharacters = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException, EmptyStackException, SaxEndParserEarlyException {
        if (RSS.equalsIgnoreCase(qName)) {
            tagStack.pop();
        }
        else if (CHANNEL.equalsIgnoreCase(qName)) {
            tagStack.pop();
        }
        else if (ITEM.equalsIgnoreCase(qName)) {
            if (verifyFeedItem(item)) {
                if (bRefresh && item.getEnclosure().getUrl().equalsIgnoreCase(recentEnclosureUrl)) {
                    throw new SaxEndParserEarlyException();
                }
                feed.getFeedItems().add(item);
            }
            tagStack.pop();
        } else if (TITLE.equalsIgnoreCase(qName)) {
            if (CHANNEL.equalsIgnoreCase(tagStack.peek())) {
                feed.setTitle(stringBuilder.toString());
            } else if (ITEM.equalsIgnoreCase(tagStack.peek())) {
                item.setTitle(stringBuilder.toString());
            } else {
                throw new SAXException("Invalid title placement");
            }
        } else if (DESCRIPTION.equalsIgnoreCase(qName)) {
            if (CHANNEL.equalsIgnoreCase(tagStack.peek())) {
                feed.setDescription(stringBuilder.toString());
            }
            else if (ITEM.equalsIgnoreCase(tagStack.peek())) {
                item.setDescription(stringBuilder.toString());
            }
            else {
                throw new SAXException("Invalid description placement");
            }
        } else if (LINK.equalsIgnoreCase(qName)) {
            if (CHANNEL.equalsIgnoreCase(tagStack.peek())) {
                feed.setLink(stringBuilder.toString());
            }
            else if (ITEM.equalsIgnoreCase(tagStack.peek())) {
                item.setLink(stringBuilder.toString());
            }
            else {
                throw new SAXException("Invalid link placement");
            }
        } else if (PUBDATE.equalsIgnoreCase(qName)) {
            if (CHANNEL.equalsIgnoreCase(tagStack.peek())) {

            }
            else if (ITEM.equalsIgnoreCase(tagStack.peek())) {
                item.setPubDate(dateTimeFormatter.withLocale(locale).parseDateTime(stringBuilder.toString()).toString());
            }
            else {
                throw new SAXException("Invalid pubDate placement");
            }
        }
        stringBuilder.setLength(0);
        bCharacters = false;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (bCharacters) {
            stringBuilder.append(ch, start, length);
        }
    }

    public Feed getFeed() {
        return feed;
    }

    // TODO: Update so that only necessary fields are checked
    private boolean verifyFeedItem(FeedItem item) {
        boolean valid = true;
        try {
            //valid = (item.getEnclosure().getLength() != null);
            valid = valid && (item.getEnclosure().getUrl() != null);
            valid = valid && (item.getEnclosure().getType() != null);
        } catch (NullPointerException e) {
            return false;
        }
        valid = valid && (item.getTitle() != null);
        valid = valid && (item.getDescription() != null);
        valid = valid && (item.getPubDate() != null);
        valid = valid && (item.getLink() != null);

        if (item.getImageUrl() == null) {
            item.setImageUrl(feed.getImageUrl());
        }

        return valid;
    }
}
