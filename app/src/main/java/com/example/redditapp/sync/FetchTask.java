package com.example.redditapp.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.redditapp.data.RedditContract;
import com.example.redditapp.model.Post;
import com.example.redditapp.utilities.JsonUtils;
import com.example.redditapp.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

class FetchTask {
    synchronized static void fetchPosts(Context context) {
        try {
            ArrayList<String> subreddits = new ArrayList<>();
            Cursor cursor = context.getContentResolver().query(RedditContract.SubredditEntry.CONTENT_URI,
                    null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    subreddits.add(cursor.getString(cursor.getColumnIndex("name")));
                } while (cursor.moveToNext());
                cursor.close();
            }
            for (String subreddit : subreddits) {
                URL url = NetworkUtils.buildUrlWithSubreddit(subreddit);
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                ArrayList<Post> posts = JsonUtils.getPostListFromJson(jsonResponse);
                if (posts != null) {
                    ContentValues[] valuesArray = new ContentValues[posts.size()];
                    for (int i = 0; i < posts.size(); i++) {
                        Post post = posts.get(i);
                        ContentValues values = new ContentValues();
                        values.put(RedditContract.PostEntry.COLUMN_TITLE, post.getTitle());
                        values.put(RedditContract.PostEntry.COLUMN_AUTHOR, post.getAuthor());
                        values.put(RedditContract.PostEntry.COLUMN_SUBREDDIT, post.getSubreddit());
                        values.put(RedditContract.PostEntry.COLUMN_THUMBNAIL, post.getThumbnail());
                        values.put(RedditContract.PostEntry.COLUMN_URL, post.getUrl());
                        values.put(RedditContract.PostEntry.COLUMN_PERMALINK, post.getPermalink());
                        valuesArray[i] = values;
                    }
                    ContentResolver contentResolver = context.getContentResolver();


                    contentResolver.delete(
                            RedditContract.PostEntry.CONTENT_URI,
                            "subreddit=?",
                            new String[]{posts.get(0).getSubreddit()});


                    contentResolver.bulkInsert(
                            RedditContract.PostEntry.CONTENT_URI,
                            valuesArray);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
