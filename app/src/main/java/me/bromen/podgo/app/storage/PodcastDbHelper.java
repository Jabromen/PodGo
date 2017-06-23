package me.bromen.podgo.app.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import me.bromen.podgo.extras.structures.Feed;
import me.bromen.podgo.extras.structures.FeedItem;
import me.bromen.podgo.extras.structures.FeedList;

/**
 * Created by jeff on 5/27/17.
 */

public class PodcastDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATBASE_NAME = "Podcast.db";

    public PodcastDbHelper(Context context) {
        super(context, DATBASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PodcastDbContract.SQL_CREATE_TABLE_FEEDS);
        db.execSQL(PodcastDbContract.SQL_CREATE_TABLE_FEED_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PodcastDbContract.SQL_DELETE_TABLE_FEEDS);
        db.execSQL(PodcastDbContract.SQL_DELETE_TABLE_FEED_ITEMS);
        onCreate(db);
    }

    public boolean saveFeed(Feed feed) {

        if (getFeedCount(feed.getTitle()) > 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PodcastDbContract.KEY_TITLE, feed.getTitle());
        values.put(PodcastDbContract.KEY_FEEDURL, feed.getFeedUrl());
        values.put(PodcastDbContract.KEY_IMAGEURL, feed.getImageUrl());
        values.put(PodcastDbContract.KEY_DESCRIPTION, feed.getDescription());
        values.put(PodcastDbContract.KEY_LINK, feed.getLink());

        long id = db.insert(PodcastDbContract.TABLE_NAME_FEED, null, values);

        for (FeedItem item: feed.getFeedItems()) {
            saveFeedItem(item, id);
        }

        return true;
    }

    public int updateFeed(Feed feed) {

        int newItems = 0;
        long id = getFeedId(feed.getTitle());

        for (FeedItem item: feed.getFeedItems()) {
            if (getFeedItemCount(id, item.getTitle()) != 0) {
                break;
            }
            saveFeedItem(item, id);
            newItems++;
        }

        return newItems;
    }

    public void saveFeedItem(FeedItem item, long id) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PodcastDbContract.KEY_ID, id);
        values.put(PodcastDbContract.KEY_TITLE, item.getTitle());
        values.put(PodcastDbContract.KEY_DESCRIPTION, item.getDescription());
        values.put(PodcastDbContract.KEY_LINK, item.getLink());
        values.put(PodcastDbContract.KEY_PUBDATE, item.getPubDate());
        if (item.getEnclosure() != null) {
            values.put(PodcastDbContract.KEY_ENCLOSUREURL, item.getEnclosure().getUrl());
            values.put(PodcastDbContract.KEY_ENCLOSURETYPE, item.getEnclosure().getType());
            values.put(PodcastDbContract.KEY_ENCLOSURELENGTH, item.getEnclosure().getLength());
        }
        values.put(PodcastDbContract.KEY_FEEDPLACE, item.getId());

        db.insert(PodcastDbContract.TABLE_NAME_FEED_ITEMS, null, values);
    }

    public int getFeedCount(String title) {
        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + PodcastDbContract.TABLE_NAME_FEED +
                    " WHERE " + PodcastDbContract.KEY_TITLE + " = ?";

            c = db.rawQuery(query, new String[] {title});
            if (c.moveToFirst()) {
                return c.getInt(0);
            } else {
                return 0;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public int getFeedItemCount(long id, String title) {
        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + PodcastDbContract.TABLE_NAME_FEED_ITEMS +
                    " WHERE " + PodcastDbContract.KEY_ID + " = ?" +
                    " AND " + PodcastDbContract.KEY_TITLE + " = ?";

            c = db.rawQuery(query, new String[] {Long.toString(id), title});
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
            else {
                return 0;
            }
        }
        finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public long getFeedId(String title) {

        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();

            String query = "SELECT " + PodcastDbContract.KEY_ID + " FROM " +
                    PodcastDbContract.TABLE_NAME_FEED + " WHERE " +
                    PodcastDbContract.KEY_TITLE + " = ?";

            c = db.rawQuery(query, new String[] {title});
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
            else {
                return -1;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public FeedList loadAllFeeds(int order) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                PodcastDbContract.KEY_ID,
                PodcastDbContract.KEY_TITLE,
                PodcastDbContract.KEY_DESCRIPTION,
                PodcastDbContract.KEY_LINK,
                PodcastDbContract.KEY_FEEDURL,
                PodcastDbContract.KEY_IMAGEURL
        };

        String sortOrder;

        switch (order) {
            case PodcastDbContract.ORDER_ALPHA_ASC:
                sortOrder = PodcastDbContract.KEY_TITLE + " ASC"; break;

            case PodcastDbContract.ORDER_ALPHA_DESC:
                sortOrder = PodcastDbContract.KEY_TITLE + " DESC"; break;

            default:
                sortOrder = null; break;
        }

        Cursor cursor = null;
        FeedList feedList = null;
        try {
            cursor = db.query(PodcastDbContract.TABLE_NAME_FEED, projection,
                    null, null, null, null, sortOrder);

            feedList = new FeedList(cursor);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return feedList;
    }

    public Feed loadFeed(long id) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                PodcastDbContract.KEY_ID,
                PodcastDbContract.KEY_TITLE,
                PodcastDbContract.KEY_DESCRIPTION,
                PodcastDbContract.KEY_LINK,
                PodcastDbContract.KEY_FEEDURL,
                PodcastDbContract.KEY_IMAGEURL
        };

        String selection = PodcastDbContract.KEY_ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};

        Cursor c = null;
        try {
            c = db.query(PodcastDbContract.TABLE_NAME_FEED, projection, selection,
                    selectionArgs, null, null, null);

            if (c.moveToFirst()) {
                return new Feed(c);
            } else {
                return null;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public List<FeedItem> loadFeedItems(long id) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                PodcastDbContract.KEY_FEEDPLACE,
                PodcastDbContract.KEY_TITLE,
                PodcastDbContract.KEY_DESCRIPTION,
                PodcastDbContract.KEY_PUBDATE,
                PodcastDbContract.KEY_LINK,
                PodcastDbContract.KEY_ENCLOSUREURL,
                PodcastDbContract.KEY_ENCLOSURETYPE,
                PodcastDbContract.KEY_ENCLOSURELENGTH
        };

        String selection = PodcastDbContract.KEY_ID + " = ?";
        String[] selectId = {Long.toString(id)};

        String sortOrder = "date(" + PodcastDbContract.KEY_PUBDATE + ") DESC";

        Cursor cursor = null;
        List<FeedItem> feedItems = new ArrayList<>();
        try {
            cursor = db.query(PodcastDbContract.TABLE_NAME_FEED_ITEMS,
                    projection, selection, selectId, null, null, sortOrder);

            while (cursor.moveToNext()) {
                feedItems.add(new FeedItem(cursor));
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return feedItems;
    }

    public boolean deleteFeed(long id) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(PodcastDbContract.TABLE_NAME_FEED_ITEMS, PodcastDbContract.KEY_ID + " = " + Long.toString(id), null);
        db.delete(PodcastDbContract.TABLE_NAME_FEED, PodcastDbContract.KEY_ID + " = " + Long.toString(id), null);

        return true;
    }
}
