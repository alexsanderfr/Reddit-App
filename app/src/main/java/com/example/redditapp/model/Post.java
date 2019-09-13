package com.example.redditapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Post implements Parcelable {

    private String title;
    private String url;
    private String subreddit;
    private String author;
    private String permalink;
    private String selftext;
    private String image;
    private String gif;
    private String thumbnail;
    private String postHint;
    private ArrayList<Comment> comments;

    public Post() {
    }

    private Post(Parcel p) {
        title = p.readString();
        url = p.readString();
        subreddit = p.readString();
        author = p.readString();
        permalink = p.readString();
        selftext = p.readString();
        image = p.readString();
        gif = p.readString();
        thumbnail = p.readString();
        postHint = p.readString();
        p.readList(comments, null);
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(subreddit);
        dest.writeString(author);
        dest.writeString(permalink);
        dest.writeString(selftext);
        dest.writeString(image);
        dest.writeString(gif);
        dest.writeString(thumbnail);
        dest.writeString(postHint);
        dest.writeList(comments);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getSelftext() {
        return selftext;
    }

    public void setSelftext(String selftext) {
        this.selftext = selftext;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public String getGif() {
        return gif;
    }

    public void setGif(String gif) {
        this.gif = gif;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPostHint() {
        return postHint;
    }

    public void setPostHint(String postHint) {
        this.postHint = postHint;
    }
}
