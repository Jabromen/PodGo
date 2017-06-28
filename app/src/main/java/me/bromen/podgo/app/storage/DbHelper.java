package me.bromen.podgo.app.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import me.bromen.podgo.extras.structures.Feed;
import me.bromen.podgo.extras.structures.FeedItem;

/**
 * Created by jeff on 5/27/17.
 */

public class DbHelper extends SQLiteOpenHelper {

    // Database info
    public static final int DATABASE_VERSION = 1;
    public static final String DATBASE_NAME = "Podcast.db";

    // Order Options
    public static final int ORDER_ALPHA_ASC = 0;
    public static final int ORDER_ALPHA_DESC = 1;

    public DbHelper(Context context) {
        super(context, DATBASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbContract.SQL_CREATE_TABLE_FEEDS);
        db.execSQL(DbContract.SQL_CREATE_TABLE_FEED_ITEMS);
        db.execSQL(DbContract.SQL_CREATE_TABLE_ITEM_DOWNLOADS);
        db.execSQL(DbContract.SQL_CREATE_TABLE_ITEM_STORAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DbContract.SQL_DELETE_TABLE_FEEDS);
        db.execSQL(DbContract.SQL_DELETE_TABLE_FEED_ITEMS);
        db.execSQL(DbContract.SQL_CREATE_TABLE_ITEM_DOWNLOADS);
        db.execSQL(DbContract.SQL_DELETE_TABLE_ITEM_STORAGE);
        onCreate(db);
    }

    /**
     * Saves a new feed into the table
     * @param feed new feed being saved
     * @return true if saved, false if feed with the same title is already saved
     */
    public boolean saveFeed(Feed feed) {

        if (getFeedCount(feed.getTitle()) > 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.KEY_TITLE, feed.getTitle());
        values.put(DbContract.KEY_FEEDURL, feed.getFeedUrl());
        values.put(DbContract.KEY_IMAGEURL, feed.getImageUrl());
        values.put(DbContract.KEY_DESCRIPTION, feed.getDescription());
        values.put(DbContract.KEY_LINK, feed.getLink());

        long id = db.insert(DbContract.TABLE_NAME_FEED, null, values);

        for (FeedItem item: feed.getFeedItems()) {
            saveFeedItem(item, id);
        }

        return true;
    }

    /**
     * Saves newly added feed items to the table
     * @param feed newly parsed feed
     * @return the number of new items saved
     */
    public int updateFeed(Feed feed) {

        int newItems = 0;
        long id = getFeedId(feed.getTitle());

        for (FeedItem item: feed.getFeedItems()) {
            if (getFeedItemCount(id, item.getEnclosure().getUrl()) != 0) {
                break;
            }
            saveFeedItem(item, id);
            newItems++;
        }

        return newItems;
    }

    /**
     * Insert a feed item into the table
     * @param item feed item being inserted
     * @param id database ID of parent feed
     */
    public void saveFeedItem(FeedItem item, long id) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.KEY_ID, id);
        values.put(DbContract.KEY_TITLE, item.getTitle());
        values.put(DbContract.KEY_DESCRIPTION, item.getDescription());
        values.put(DbContract.KEY_LINK, item.getLink());
        values.put(DbContract.KEY_PUBDATE, item.getPubDate());
        if (item.getEnclosure() != null) {
            values.put(DbContract.KEY_ENCLOSUREURL, item.getEnclosure().getUrl());
            values.put(DbContract.KEY_ENCLOSURETYPE, item.getEnclosure().getType());
            values.put(DbContract.KEY_ENCLOSURELENGTH, item.getEnclosure().getLength());
        }

        db.insert(DbContract.TABLE_NAME_FEED_ITEMS, null, values);
    }

    /**
     * Used to check if a feed is already saved
     * @param title feed title, used to identify a feed without knowing a database ID
     * @return the number of saved feeds found
     */
    public int getFeedCount(String title) {
        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + DbContract.TABLE_NAME_FEED +
                    " WHERE " + DbContract.KEY_TITLE + " = ?";

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

    /**
     * Used to check if a feed item is already saved.
     * @param feedId database ID of parent feed
     * @param enclosureUrl url of audio file,
     *                     used to uniquely identify feed items without knowing a database ID
     * @return the number of saved items found
     */
    public int getFeedItemCount(long feedId, String enclosureUrl) {
        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + DbContract.TABLE_NAME_FEED_ITEMS +
                    " WHERE " + DbContract.KEY_ID + " = ?" +
                    " AND " + DbContract.KEY_ENCLOSUREURL + " = ?";

            c = db.rawQuery(query, new String[] {Long.toString(feedId), enclosureUrl});
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

    /**
     * Gets the database ID of a newly parsed feed from its title
     * @param title title of feed
     * @return database ID if found, -1 if not found
     */
    public long getFeedId(String title) {

        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();

            String query = "SELECT " + DbContract.KEY_ID + " FROM " +
                    DbContract.TABLE_NAME_FEED + " WHERE " +
                    DbContract.KEY_TITLE + " = ?";

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

    public List<Feed> loadAllFeeds(int order) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                DbContract.KEY_ID,
                DbContract.KEY_TITLE,
                DbContract.KEY_DESCRIPTION,
                DbContract.KEY_LINK,
                DbContract.KEY_FEEDURL,
                DbContract.KEY_IMAGEURL
        };

        String sortOrder;

        switch (order) {
            case ORDER_ALPHA_ASC:
                sortOrder = DbContract.KEY_TITLE + " ASC";
                break;
            case ORDER_ALPHA_DESC:
                sortOrder = DbContract.KEY_TITLE + " DESC";
                break;
            default:
                sortOrder = null;
                break;
        }

        Cursor cursor = null;
        List<Feed> feedList = new ArrayList<>();
        try {
            cursor = db.query(DbContract.TABLE_NAME_FEED, projection,
                    null, null, null, null, sortOrder);

            while (cursor.moveToNext()) {
                feedList.add(new Feed(cursor));
            }
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
                DbContract.KEY_ID,
                DbContract.KEY_TITLE,
                DbContract.KEY_DESCRIPTION,
                DbContract.KEY_LINK,
                DbContract.KEY_FEEDURL,
                DbContract.KEY_IMAGEURL
        };

        String selection = DbContract.KEY_ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};

        Cursor c = null;
        try {
            c = db.query(DbContract.TABLE_NAME_FEED, projection, selection,
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
                DbContract.KEY_ITEMID,
                DbContract.KEY_TITLE,
                DbContract.KEY_DESCRIPTION,
                DbContract.KEY_PUBDATE,
                DbContract.KEY_LINK,
                DbContract.KEY_ENCLOSUREURL,
                DbContract.KEY_ENCLOSURETYPE,
                DbContract.KEY_ENCLOSURELENGTH
        };

        String selection = DbContract.KEY_ID + " = ?";
        String[] selectId = {Long.toString(id)};

        String sortOrder = "date(" + DbContract.KEY_PUBDATE + ") DESC";

        Cursor cursor = null;
        List<FeedItem> feedItems = new ArrayList<>();
        try {
            cursor = db.query(DbContract.TABLE_NAME_FEED_ITEMS,
                    projection, selection, selectId, null, null, sortOrder);

            while (cursor.moveToNext()) {
                FeedItem item = new FeedItem(cursor);

                item.setDownloaded(isDownloaded(item.getId()));
                item.setDownloading(isDownloading(item.getId()));

                feedItems.add(item);
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return feedItems;
    }

    private boolean isDownloaded(long id) {
        return getFilename(id) != null;
    }

    public String getFilename(long id) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = { DbContract.KEY_FILENAME };

        String selection = DbContract.KEY_ITEMID + " = ?";
        String[] selectionArgs = { Long.toString(id) };

        Cursor cursor = null;
        try {
            cursor = db.query(DbContract.TABLE_NAME_ITEM_STORAGE,
                    projection, selection, selectionArgs, null, null, null);

            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            } else {
                return null;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<String> getFilenames() {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = { DbContract.KEY_FILENAME };

        Cursor c = null;
        List<String> filenames = new ArrayList<>();
        try {
            c = db.query(DbContract.TABLE_NAME_ITEM_STORAGE, projection, null, null, null, null, null);

            while (c.moveToNext()) {
                filenames.add(c.getString(0));
            }

            return filenames;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private boolean isDownloading(long id) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT COUNT(*) FROM " + DbContract.TABLE_NAME_ITEM_DOWNLOADS +
                " WHERE " + DbContract.KEY_ITEMID + " = ?";
        String[] args = { Long.toString(id) };

        Cursor c = null;
        try {
            c = db.rawQuery(query, args);

            return c.moveToFirst() && c.getInt(0) > 0;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public Long getDownloadId(long itemId) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT " + DbContract.KEY_DOWNLOADID +
                " FROM " + DbContract.TABLE_NAME_ITEM_DOWNLOADS +
                " WHERE " + DbContract.KEY_ITEMID + " = ?";

        String[] args = { Long.toString(itemId) };

        Cursor c = null;
        try {
            c = db.rawQuery(query, args);

            if (c.moveToFirst()) {
                return c.getLong(0);
            } else {
                return null;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public List<Long> getDownloadIds() {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT " + DbContract.KEY_DOWNLOADID +
                " FROM " + DbContract.TABLE_NAME_ITEM_DOWNLOADS;

        Cursor c = null;
        List<Long> downloadIds = new ArrayList<>();
        try {
            c = db.rawQuery(query, null);

            while (c.moveToNext()) {
                downloadIds.add(c.getLong(0));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return downloadIds;
    }

    public void saveDownloading(long itemId, long downloadId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.KEY_ITEMID, itemId);
        values.put(DbContract.KEY_DOWNLOADID, downloadId);

        db.insert(DbContract.TABLE_NAME_ITEM_DOWNLOADS, null, values);
    }

    public void saveStorage(long itemId, String filename) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.KEY_ITEMID, itemId);
        values.put(DbContract.KEY_FILENAME, filename);

        db.insert(DbContract.TABLE_NAME_ITEM_STORAGE, null, values);
    }

    public void deleteDownloadId(long downloadId) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DbContract.TABLE_NAME_ITEM_DOWNLOADS, DbContract.KEY_DOWNLOADID + " = " + Long.toString(downloadId), null);
    }

    public void deleteStorage(long itemId) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DbContract.TABLE_NAME_ITEM_STORAGE, DbContract.KEY_ITEMID + " = " + Long.toString(itemId), null);
    }

    public void deleteStorageFromFilename(String filename) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DbContract.TABLE_NAME_ITEM_STORAGE, DbContract.KEY_FILENAME + " = '" + filename + "'", null);
    }

    public boolean deleteFeed(long id) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DbContract.TABLE_NAME_FEED_ITEMS, DbContract.KEY_ID + " = " + Long.toString(id), null);
        db.delete(DbContract.TABLE_NAME_FEED, DbContract.KEY_ID + " = " + Long.toString(id), null);

        return true;
    }
}
