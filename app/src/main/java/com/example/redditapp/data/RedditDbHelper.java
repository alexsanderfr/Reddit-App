package com.example.redditapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RedditDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "reddit.db";
    private static final int VERSION = 6;

    public RedditDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE_SUBREDDITS = "CREATE TABLE " + RedditContract.SubredditEntry.TABLE_NAME + " (" +
                RedditContract.SubredditEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RedditContract.SubredditEntry.COLUMN_NAME + " TEXT NOT NULL " +
                ");";
        final String CREATE_TABLE_POSTS = "CREATE TABLE " + RedditContract.PostEntry.TABLE_NAME + " (" +
                RedditContract.PostEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RedditContract.PostEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                RedditContract.PostEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                RedditContract.PostEntry.COLUMN_SUBREDDIT + " TEXT NOT NULL, " +
                RedditContract.PostEntry.COLUMN_THUMBNAIL + " TEXT NOT NULL, " +
                RedditContract.PostEntry.COLUMN_URL + " TEXT NOT NULL, " +
                RedditContract.PostEntry.COLUMN_PERMALINK + " TEXT NOT NULL " +
                ");";
        db.execSQL(CREATE_TABLE_SUBREDDITS);
        db.execSQL(CREATE_TABLE_POSTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RedditContract.SubredditEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RedditContract.PostEntry.TABLE_NAME);
        onCreate(db);
    }
}
