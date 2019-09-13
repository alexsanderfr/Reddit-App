package com.example.redditapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Subreddit implements Parcelable {
    private String name;
    private String url;
    private String description;
    private String subscribers;
    private String headerTitle;

    public Subreddit() {
    }

    private Subreddit(Parcel in) {
        name = in.readString();
        url = in.readString();
        description = in.readString();
        subscribers = in.readString();
        headerTitle = in.readString();
    }

    public static final Creator<Subreddit> CREATOR = new Creator<Subreddit>() {
        @Override
        public Subreddit createFromParcel(Parcel in) {
            return new Subreddit(in);
        }

        @Override
        public Subreddit[] newArray(int size) {
            return new Subreddit[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(description);
        dest.writeString(subscribers);
        dest.writeString(headerTitle);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(String subscribers) {
        this.subscribers = subscribers;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }
}
