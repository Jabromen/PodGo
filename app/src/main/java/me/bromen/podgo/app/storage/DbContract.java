package me.bromen.podgo.app.storage;

/**
 * Created by jeff on 5/27/17.
 */

public class DbContract {

    private DbContract() {}

    // Table Names
    public static final String TABLE_NAME_FEED = "feeds";
    public static final String TABLE_NAME_FEED_ITEMS = "feed_items";

    // Column keys
    public static final String KEY_ID = "id";
    public static final String KEY_ITEMID = "itemId";
    public static final String KEY_DOWNLOADID = "downloadId";
    public static final String KEY_TITLE = "title";
    public static final String KEY_FEEDURL = "feedUrl";
    public static final String KEY_IMAGEURL = "imageUrl";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_LINK = "link";
    public static final String KEY_PUBDATE = "pubDate";
    public static final String KEY_ENCLOSUREURL = "enclosureUrl";
    public static final String KEY_ENCLOSURETYPE = "enclosureType";
    public static final String KEY_ENCLOSURELENGTH = "enclosureLength";
    public static final String KEY_FILENAME = "filename";

    // Create tables
    public static final String SQL_CREATE_TABLE_FEEDS =
            "CREATE TABLE " + TABLE_NAME_FEED + " (" + KEY_ID + " INTEGER PRIMARY KEY," +
                    KEY_TITLE + " TEXT," + KEY_DESCRIPTION + " TEXT," + KEY_LINK + " TEXT," +
                    KEY_FEEDURL + " TEXT," + KEY_IMAGEURL + " TEXT," + KEY_ENCLOSUREURL + " TEXT)";

    public static final String SQL_CREATE_TABLE_FEED_ITEMS =
            "CREATE TABLE " + TABLE_NAME_FEED_ITEMS + " (" + KEY_ITEMID + " INTEGER PRIMARY KEY," +
                    KEY_ID + " INTEGER," + KEY_TITLE + " TEXT," +
                    KEY_DESCRIPTION + " TEXT," + "" + KEY_PUBDATE + " TEXT," +
                    KEY_LINK + " TEXT," + KEY_ENCLOSUREURL + " TEXT," +
                    KEY_ENCLOSURETYPE + " TEXT," + KEY_ENCLOSURELENGTH + " TEXT," +
                    KEY_FILENAME + " TEXT," + KEY_DOWNLOADID + " INTEGER," +
                    KEY_IMAGEURL + " TEXT)";

    // Delete tables
    public static final String SQL_DELETE_TABLE_FEEDS =
            "DROP TABLE IF EXISTS " + TABLE_NAME_FEED;

    public static final String SQL_DELETE_TABLE_FEED_ITEMS =
            "DROP TABLE IF EXISTS " + TABLE_NAME_FEED_ITEMS;
}
