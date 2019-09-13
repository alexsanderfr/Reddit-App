package com.example.redditapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import com.example.redditapp.R;
import com.example.redditapp.data.RedditContract;
import com.example.redditapp.model.Post;
import com.example.redditapp.ui.MainActivity;

import java.util.ArrayList;

public class LastSelectedAppWidgetProvider extends AppWidgetProvider {
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, LastSelectedWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            SharedPreferences sharedPreferences = context
                    .getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String subreddit = sharedPreferences.getString(context.getString(R.string.subreddit), "FrontPage");

            ArrayList<Post> posts = new ArrayList<>();
            Cursor cursor = context.getContentResolver().query(RedditContract.PostEntry.CONTENT_URI,
                    null, "subreddit=?",
                    new String[]{subreddit},
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Post post = new Post();
                    post.setTitle(cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_TITLE)));
                    post.setAuthor(cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_AUTHOR)));
                    post.setSubreddit(cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_SUBREDDIT)));
                    post.setPermalink(cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_PERMALINK)));
                    posts.add(post);
                } while (cursor.moveToNext());
                cursor.close();
            }

            String[] titles = new String[posts.size()];
            String[] authors = new String[posts.size()];
            String[] permalinks = new String[posts.size()];
            for (int i = 0; i<posts.size();i++) {
                titles[i] = posts.get(i).getTitle();
                authors[i] = posts.get(i).getAuthor();
                permalinks[i] = posts.get(i).getPermalink();
            }

            intent.putExtra("titles", titles);
            intent.putExtra("authors", authors);
            intent.putExtra("subreddit", subreddit);
            intent.putExtra("permalinks", permalinks);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.last_selected_widget);
            views.setTextViewText(R.id.last_selected_tv_widget, "Last visited subreddit: " + subreddit);
            views.setViewVisibility(R.id.last_selected_tv_widget, View.VISIBLE);
            views.setViewVisibility(R.id.widget_list_view, View.VISIBLE);
            views.setViewVisibility(R.id.error_tv_widget, View.GONE);

            Intent mainIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent
                    .getActivity(context, 0, mainIntent, 0);
            views.setOnClickPendingIntent(R.id.last_selected_tv_widget, pendingIntent);

            views.setRemoteAdapter(appWidgetId, R.id.widget_list_view, intent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
