package com.example.redditapp.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {
    private static final String REDDIT_BASE_URL = "https://www.reddit.com";
    private static final String SUBREDDITS_PATH = "/subreddits";
    private static final String SEARCH_PATH = "/search";
    private static final String HOT_PATH = "/hot";
    private static final String SUBREDDIT_PATH = "/r";
    private static final String JSON_EXTENSION = ".json";
    private static final String QUERY_PARAM = "q";

    public static URL buildUrlWithSubreddit(String subredditName) {
        String baseUrl = REDDIT_BASE_URL;
        if (subredditName != null && !subredditName.equals("")) {
            baseUrl = baseUrl.concat(SUBREDDIT_PATH).concat("/"+subredditName).concat(HOT_PATH).concat(JSON_EXTENSION);
        } else {
            baseUrl = baseUrl.concat("/"+JSON_EXTENSION);
        }
        URL url = null;
        try {
            url = new URL(baseUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrlWithPostUrl(String postUrl) {
        String baseUrl = postUrl.concat(JSON_EXTENSION);
        URL url = null;
        try {
            url = new URL(baseUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrlWithPermalink(String permalink) {
        String baseUrl = REDDIT_BASE_URL.concat(permalink).concat(JSON_EXTENSION);
        URL url = null;
        try {
            url = new URL(baseUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String buildUrlForSharingWithPermalink(String permalink) {
        String baseUrl = REDDIT_BASE_URL.concat(permalink);
        URL url = null;
        try {
            url = new URL(baseUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url != null) {
            return url.toString();
        } else {
            return null;
        }
    }

    public static URL buildUrlForSearch(String query) {
        String baseUrl = REDDIT_BASE_URL.concat(SUBREDDITS_PATH).concat(SEARCH_PATH).concat(JSON_EXTENSION);
        Uri uri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(QUERY_PARAM, query)
                .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
