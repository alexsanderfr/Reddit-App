package com.example.redditapp.utilities;

import com.example.redditapp.model.Comment;
import com.example.redditapp.model.Post;
import com.example.redditapp.model.Subreddit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public final  class JsonUtils {

    private final static String OWM_MESSAGE_CODE = "cod";
    private final static String OWM_CHILDREN = "children";
    private final static String OWM_DATA = "data";
    private final static String OWM_TITLE = "title";
    private final static String OWM_URL = "url";
    private final static String OWM_PERMALINK = "permalink";
    private final static String OWM_SUBREDDIT = "subreddit";
    private final static String OWM_AUTHOR = "author";
    private final static String OWM_SELFTEXT = "selftext";
    private final static String OWM_PREVIEW = "preview";
    private final static String OWM_IMAGES = "images";
    private final static String OWM_SOURCE = "source";
    private final static String OWM_BODY = "body";
    private final static String OWM_KIND = "kind";
    private final static String OWM_REPLIES = "replies";
    private final static String OWM_DISPLAY_NAME = "display_name";
    private final static String OWM_DESCRIPTION = "description";
    private final static String OWM_SUBSCRIBERS = "subscribers";
    private final static String OWM_HEADER_TITLE = "header_title";
    private final static String OWM_VARIANTS = "variants";
    private final static String OWM_GIF = "gif";
    private final static String OWM_THUMBNAIL = "thumbnail";
    private final static String OWM_POST_HINT = "post_hint";
    public final static String HINT_IMAGE = "image";
    public final static String HINT_LINK = "link";
    public final static String HINT_RICH_VIDEO = "rich:video";


    public static ArrayList<Post> getPostListFromJson(String subredditJsonStr) throws JSONException {

        ArrayList<Post> posts = new ArrayList<>();
        JSONObject subredditJson = new JSONObject(subredditJsonStr);

        if (subredditJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = subredditJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }
        JSONObject subredditDataJsonObject = subredditJson.getJSONObject(OWM_DATA);
        JSONArray childrenJsonArray = subredditDataJsonObject.getJSONArray(OWM_CHILDREN);

        for (int i = 0; i < childrenJsonArray.length(); i++) {
            JSONObject postJsonObject = childrenJsonArray.getJSONObject(i);
            JSONObject childrenDataJsonObject = postJsonObject.getJSONObject(OWM_DATA);
            String title = childrenDataJsonObject.getString(OWM_TITLE);
            String url = childrenDataJsonObject.getString(OWM_URL);
            String subreddit = childrenDataJsonObject.getString(OWM_SUBREDDIT);
            String author = childrenDataJsonObject.getString(OWM_AUTHOR);
            String permalink = childrenDataJsonObject.getString(OWM_PERMALINK);
            String thumbnail = childrenDataJsonObject.getString(OWM_THUMBNAIL);
            Post post = new Post();
            post.setTitle(title);
            post.setUrl(url);
            post.setSubreddit(subreddit);
            post.setAuthor(author);
            post.setPermalink(permalink);
            post.setThumbnail(thumbnail);
            posts.add(post);
        }
        return posts;
    }

    public static Post getPostDetailFromJson(String postJsonStr) throws JSONException {

        Post post = new Post();
        JSONArray postJsonArray = new JSONArray(postJsonStr);
        JSONObject postJsonObject = postJsonArray.getJSONObject(0);
        JSONObject postDataJsonObject = postJsonObject.getJSONObject(OWM_DATA);
        JSONArray postChildrenJsonArray = postDataJsonObject.getJSONArray(OWM_CHILDREN);
        JSONObject postChildrenJsonObject = postChildrenJsonArray.getJSONObject(0);
        JSONObject postChildrenDataJsonObject = postChildrenJsonObject.getJSONObject(OWM_DATA);
        String title = postChildrenDataJsonObject.getString(OWM_TITLE);
        post.setTitle(title);
        String permalink = postChildrenDataJsonObject.getString(OWM_PERMALINK);
        post.setPermalink(permalink);
        String postHint = null;
        try {
            postHint = postChildrenDataJsonObject.getString(OWM_POST_HINT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        post.setPostHint(postHint);
        if (postHint == null) {
            String selfText = postChildrenDataJsonObject.getString(OWM_SELFTEXT);
            post.setSelftext(selfText);
        } else if (postHint.equals(HINT_LINK)) {
            String url = postChildrenDataJsonObject.getString(OWM_URL);
            post.setUrl(url);
        } else if (postHint.equals(HINT_IMAGE)) {
            try {
                JSONObject preview = postChildrenDataJsonObject.getJSONObject(OWM_PREVIEW);
                JSONArray images = preview.getJSONArray(OWM_IMAGES);
                JSONObject imageJsonObject = images.getJSONObject(0);
                JSONObject sourceJsonObject = imageJsonObject.getJSONObject(OWM_SOURCE);
                String image = sourceJsonObject.getString(OWM_URL);
                post.setImage(image);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (postHint.equals(HINT_RICH_VIDEO)) {
            try {
                JSONObject preview = postChildrenDataJsonObject.getJSONObject(OWM_PREVIEW);
                JSONArray images = preview.getJSONArray(OWM_IMAGES);
                JSONObject imageJsonObject = images.getJSONObject(0);
                JSONObject variantsJsonObject = imageJsonObject.getJSONObject(OWM_VARIANTS);
                JSONObject gifJsonObject = variantsJsonObject.getJSONObject(OWM_GIF);
                JSONObject gifSourceJsonObject = gifJsonObject.getJSONObject(OWM_SOURCE);
                String gif = gifSourceJsonObject.getString(OWM_URL);
                post.setGif(gif);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String thumbnail = postChildrenDataJsonObject.getString(OWM_THUMBNAIL);
        post.setThumbnail(thumbnail);
        post.setComments(getCommentsFromJsonObject(postJsonArray.getJSONObject(1)));
        return post;
    }

    private static ArrayList<Comment> getCommentsFromJsonObject(JSONObject commentsJsonObject) throws JSONException {
        ArrayList<Comment> commentArrayList = new ArrayList<>();
        JSONObject commentDataJsonObject = commentsJsonObject.getJSONObject(OWM_DATA);
        JSONArray commentChildrenJsonArray = commentDataJsonObject.getJSONArray(OWM_CHILDREN);
        for (int i = 0; i < commentChildrenJsonArray.length(); i++) {
            JSONObject commentChildrenJsonObject = commentChildrenJsonArray.getJSONObject(i);
            String kind = commentChildrenJsonObject.getString(OWM_KIND);
            if (!kind.equals("more")) {
                JSONObject commentChildrenDataJsonObject = commentChildrenJsonObject.getJSONObject(OWM_DATA);
                String commentAuthor = commentChildrenDataJsonObject.getString(OWM_AUTHOR);
                String commentBody = commentChildrenDataJsonObject.getString(OWM_BODY);
                Comment comment = new Comment();
                comment.setAuthor(commentAuthor);
                comment.setBody(commentBody);
                try {
                    JSONObject replies = commentChildrenDataJsonObject.getJSONObject(OWM_REPLIES);
                    comment.setReplies(getCommentsFromJsonObject(replies));
                } catch (JSONException e) {
                    comment.setReplies(null);
                }
                commentArrayList.add(comment);
            }
        }
        return commentArrayList;
    }

    public static ArrayList<Subreddit> getSubredditListFromJson(String searchJsonStr) throws JSONException {

        ArrayList<Subreddit> subreddits = new ArrayList<>();
        JSONObject searchJsonObject = new JSONObject(searchJsonStr);


        if (searchJsonObject.has(OWM_MESSAGE_CODE)) {
            int errorCode = searchJsonObject.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }
        JSONObject searchDataJsonObject = searchJsonObject.getJSONObject(OWM_DATA);
        JSONArray childrenJsonArray = searchDataJsonObject.getJSONArray(OWM_CHILDREN);

        for (int i = 0; i < childrenJsonArray.length(); i++) {
            JSONObject subredditResultJsonObject = childrenJsonArray.getJSONObject(i);
            JSONObject childrenDataJsonObject = subredditResultJsonObject.getJSONObject(OWM_DATA);
            String name = childrenDataJsonObject.getString(OWM_DISPLAY_NAME);
            String url = childrenDataJsonObject.getString(OWM_URL);
            String description = childrenDataJsonObject.getString(OWM_DESCRIPTION);
            String subscribers = childrenDataJsonObject.getString(OWM_SUBSCRIBERS);
            String header_title = childrenDataJsonObject.getString(OWM_HEADER_TITLE);
            Subreddit subreddit = new Subreddit();
            subreddit.setName(name);
            subreddit.setUrl(url);
            subreddit.setDescription(description);
            subreddit.setSubscribers(subscribers);
            subreddit.setHeaderTitle(header_title);
            subreddits.add(subreddit);
        }
        return subreddits;
    }
}
