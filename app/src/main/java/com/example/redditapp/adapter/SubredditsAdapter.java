package com.example.redditapp.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.redditapp.R;
import com.example.redditapp.data.RedditContract;
import com.example.redditapp.model.Subreddit;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

public class SubredditsAdapter extends RecyclerView.Adapter<SubredditsAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Subreddit> mSubreddits;
    private final SubredditsAdapterOnClickHandler mClickHandler;
    private FirebaseAnalytics mFirebaseAnalytics;

    public interface SubredditsAdapterOnClickHandler {
        void onClick(String subreddit);
    }

    public SubredditsAdapter(Context context, SubredditsAdapterOnClickHandler onClickHandler) {
        mContext = context;
        mClickHandler = onClickHandler;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @Override
    public SubredditsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        int layoutIdForListItem = R.layout.subreddit_list_item;
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SubredditsAdapter.ViewHolder holder, int position) {
        holder.mSubredditTextView.setText(mSubreddits.get(position).getName());
        String headerTitle = mSubreddits.get(position).getHeaderTitle();
        if (headerTitle == null || headerTitle.equals("") || headerTitle.equals("null")) {
            headerTitle = "This subreddit has no description";
        }
        holder.mHeaderTitleTextView.setText(headerTitle);
        String subscribersFormatted = mSubreddits.get(position).getSubscribers() + " subscribers.";
        holder.mSubscribersTextView.setText(subscribersFormatted);
    }

    @Override
    public int getItemCount() {
        if (mSubreddits == null) return 0;
        return mSubreddits.size();
    }

    public void setSubreddits(ArrayList<Subreddit> subreddits) {
        mSubreddits = subreddits;
        notifyDataSetChanged();
    }

    public ArrayList<Subreddit> getSubreddits() {
        return mSubreddits;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mSubredditTextView;
        final TextView mHeaderTitleTextView;
        final TextView mSubscribersTextView;
        final ImageButton mAddButton;
        final View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView.getRootView();
            mSubredditTextView = itemView.findViewById(R.id.subreddit_tv);
            mHeaderTitleTextView = itemView.findViewById(R.id.subreddit_header_title_tv);
            mSubscribersTextView = itemView.findViewById(R.id.subreddit_subscribers_tv);
            mAddButton = itemView.findViewById(R.id.add_bt);
            mAddButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String subredditName = mSubredditTextView.getText().toString();

            //Logs subreddits added to Firebase Analytics for future analysis of user favorite subreddits
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, subredditName);
            mFirebaseAnalytics.logEvent("add_subreddit", bundle);

            //Saves to database
            Uri uri = RedditContract.SubredditEntry.CONTENT_URI;
            ContentValues contentValues = new ContentValues();
            contentValues.put(RedditContract.SubredditEntry.COLUMN_NAME, subredditName);
            mContext.getContentResolver().insert(uri, contentValues);

            mClickHandler.onClick(subredditName);
        }
    }
}
