package com.example.redditapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class LastSelectedWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        String[] titles = intent.getStringArrayExtra("titles");
        String[] authors = intent.getStringArrayExtra("authors");
        String[] permalinks = intent.getStringArrayExtra("permalinks");
        String subreddit = intent.getStringExtra("subreddit");
        return new LastSelectedRemoteViewsFactory(this.getApplicationContext(), intent, titles, authors, permalinks, subreddit);
    }
}
