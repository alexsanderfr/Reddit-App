package com.example.redditapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class RedditContract {
    static final String AUTHORITY = "com.example.redditapp";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    static final String PATH_FAVORITES = "favorites";
    static final String PATH_POSTS = "posts";

    public static final class SubredditEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_NAME = "name";
    }

    public static final class PostEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_POSTS).build();

        public static final String TABLE_NAME = "posts";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_SUBREDDIT = "subreddit";
        public static final String COLUMN_THUMBNAIL = "thumbnail";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_PERMALINK = "permalink";
    }
}
