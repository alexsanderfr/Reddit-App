package com.example.redditapp.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;

import com.example.redditapp.R;
import com.example.redditapp.adapter.SubredditsAdapter;
import com.example.redditapp.databinding.ActivityFollowBinding;
import com.example.redditapp.model.Subreddit;
import com.example.redditapp.sync.FetchUtils;
import com.example.redditapp.utilities.JsonUtils;
import com.example.redditapp.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class FollowActivity extends AppCompatActivity implements SubredditsAdapter.SubredditsAdapterOnClickHandler {

    private ActivityFollowBinding binding;
    private LinearLayoutManager mLayoutManager;
    private SubredditsAdapter mSubredditsAdapter;
    private String mCurrentQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_follow);

        binding.searchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentQuery = binding.searchEt.getText().toString();
                if (!mCurrentQuery.equals("")) {
                    new FetchSubredditListTask().execute(mCurrentQuery);
                }
            }
        });

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.subredditsRv.setLayoutManager(mLayoutManager);
        mSubredditsAdapter = new SubredditsAdapter(this, this);
        binding.subredditsRv.setAdapter(mSubredditsAdapter);

        if (savedInstanceState != null) {
            mCurrentQuery = savedInstanceState.getString("query");
            binding.searchEt.setText(mCurrentQuery);
            ArrayList<Subreddit> savedSubreddits = savedInstanceState.getParcelableArrayList("subreddits");
            if (savedSubreddits != null) {
                binding.resultsLl.setVisibility(View.VISIBLE);
                mSubredditsAdapter.setSubreddits(savedSubreddits);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("query", mCurrentQuery);
        if (mSubredditsAdapter != null) {
            if (mSubredditsAdapter.getItemCount() > 0) {
                savedInstanceState.putParcelableArrayList("subreddits", mSubredditsAdapter.getSubreddits());
            }
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(String subreddit) {
        View contentView = findViewById(android.R.id.content);
        FetchUtils.startImmediateSync(this);
        Snackbar.make(contentView, subreddit + " was added.", Snackbar.LENGTH_SHORT)
                .show();
    }

    private class FetchSubredditListTask extends AsyncTask<String, Void, ArrayList<Subreddit>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.pbLoadingIndicator.setVisibility(View.VISIBLE);
            binding.resultsLl.setVisibility(View.INVISIBLE);
            binding.pbLoadingIndicator.getIndeterminateDrawable()
                    .setColorFilter(ResourcesCompat.getColor(getResources(), R.color.progressBarColor, null),
                            PorterDuff.Mode.MULTIPLY);
        }

        @Override
        protected ArrayList<Subreddit> doInBackground(String... params) {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = null;
            if (cm != null) {
                netInfo = cm.getActiveNetworkInfo();
            }
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                try {
                    URL url = NetworkUtils.buildUrlForSearch(params[0]);
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                    return JsonUtils.getSubredditListFromJson(jsonResponse);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Subreddit> subreddits) {
            binding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
            if (subreddits != null) {
                binding.resultsLl.setVisibility(View.VISIBLE);
                mSubredditsAdapter.setSubreddits(subreddits);
            } else {
                View view = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar.make(view, getString(R.string.connectivity_error),
                        Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
    }
}