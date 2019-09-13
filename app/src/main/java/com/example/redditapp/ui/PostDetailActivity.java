package com.example.redditapp.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;

import com.example.redditapp.R;
import com.example.redditapp.adapter.CommentsAdapter;
import com.example.redditapp.databinding.ActivityPostDetailBinding;
import com.example.redditapp.model.Post;
import com.example.redditapp.utilities.GlideApp;
import com.example.redditapp.utilities.JsonUtils;
import com.example.redditapp.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class PostDetailActivity extends AppCompatActivity {

    private ActivityPostDetailBinding binding;
    private CommentsAdapter mCommentsAdapter;
    private LinearLayoutManager mLayoutManager;
    private Post mPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_detail);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.commentsRv.setLayoutManager(mLayoutManager);
        mCommentsAdapter = new CommentsAdapter(this);
        binding.commentsRv.setAdapter(mCommentsAdapter);
        binding.commentsRv.setNestedScrollingEnabled(false);
        binding.commentsRv.setHasFixedSize(true);


        final ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        if (savedInstanceState == null) {
            mPost = getIntent().getParcelableExtra("post");
            if (mPost != null) {
                new FetchPostTask().execute(mPost.getPermalink());
            }
        } else {
             mPost = savedInstanceState.getParcelable("post");
            setupUi(mPost);

        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable("post", mPost);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupUi(final Post post) {
        binding.contentLl.setVisibility(View.VISIBLE);
        binding.detailTitleTv.setText(post.getTitle());
        if (post.getPostHint() == null) {
            binding.detailContentTv.setVisibility(View.VISIBLE);
            binding.detailContentTv.setText(post.getSelftext());
        } else if (post.getPostHint().equals(JsonUtils.HINT_LINK)) {
            binding.detailTitleTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(post.getUrl()));
                    startActivity(intent);
                }
            });
        } else if (post.getPostHint().equals(JsonUtils.HINT_IMAGE)) {
            binding.detailContentIv.setVisibility(View.VISIBLE);
            GlideApp.with(PostDetailActivity.this)
                    .load(post.getImage())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(binding.detailContentIv);
            binding.detailContentIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PostDetailActivity.this, FullscreenImageActivity.class);
                    intent.putExtra("content", post.getImage());
                    startActivity(intent);
                }
            });
        } else if (post.getPostHint().equals(JsonUtils.HINT_RICH_VIDEO)) {
            binding.detailContentIv.setVisibility(View.VISIBLE);
            GlideApp.with(PostDetailActivity.this)
                    .load(post.getGif())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(binding.detailContentIv);
            binding.detailContentIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PostDetailActivity.this, FullscreenImageActivity.class);
                    intent.putExtra("content", post.getGif());
                    startActivity(intent);
                }
            });
        } else{
            binding.commentDivider.setVisibility(View.INVISIBLE);
        }
        mCommentsAdapter.setComments(post.getComments());
    }

    public void onClickShare(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        String sharingUrl = NetworkUtils.buildUrlForSharingWithPermalink(mPost.getPermalink());
        intent.putExtra(Intent.EXTRA_TEXT, mPost.getTitle() + "\n" +  sharingUrl);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Share post!"));
    }

    private class FetchPostTask extends AsyncTask<String, Void, Post> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.contentLl.setVisibility(View.INVISIBLE);
            binding.pbLoadingIndicator.setVisibility(View.VISIBLE);
            binding.pbLoadingIndicator.getIndeterminateDrawable()
                    .setColorFilter(ResourcesCompat.getColor(getResources(), R.color.progressBarColor, null),
                            PorterDuff.Mode.MULTIPLY);
        }

        @Override
        protected Post doInBackground(String... params) {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = null;
            if (cm != null) {
                netInfo = cm.getActiveNetworkInfo();
            }
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                try {
                    URL url = NetworkUtils.buildUrlWithPermalink(params[0]);
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                    return JsonUtils.getPostDetailFromJson(jsonResponse);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Post post) {
            binding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
            if (post != null) {
                mPost = post;
                setupUi(post);
            } else {
                View view = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar.make(view, getString(R.string.connectivity_error),
                        Snackbar.LENGTH_LONG);
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new FetchPostTask().execute(mPost.getPermalink());
                    }
                };
                snackbar.setAction(R.string.retry, onClickListener);
                snackbar.show();
            }
        }
    }
}