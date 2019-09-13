package com.example.redditapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.redditapp.R;
import com.example.redditapp.model.Post;
import com.example.redditapp.ui.PostDetailActivity;

class LastSelectedRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private String[] mTitles;
    private String[] mAuthors;
    private String[] mPermalinks;
    private String mSubreddit;

    public LastSelectedRemoteViewsFactory(Context context, Intent intent, String[] titles,
                                          String[] authors, String[] permalinks, String subreddit) {
        mContext = context;
        int mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        mTitles = titles;
        mAuthors = authors;
        mPermalinks = permalinks;
        mSubreddit = subreddit;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mTitles == null) return 0;
        return mTitles.length;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        Post post = new Post();
        post.setTitle(mTitles[position]);
        post.setAuthor(mAuthors[position]);
        post.setSubreddit(mSubreddit);
        post.setPermalink(mPermalinks[position]);
        views.setTextViewText(R.id.title_tv, post.getTitle());
        views.setTextViewText(R.id.author_tv, post.getAuthor());
        views.setTextViewText(R.id.subreddit_tv, post.getSubreddit());

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
