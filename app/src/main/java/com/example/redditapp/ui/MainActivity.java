package com.example.redditapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.redditapp.R;
import com.example.redditapp.adapter.PostsAdapter;
import com.example.redditapp.data.RedditContract;
import com.example.redditapp.databinding.ActivityMainBinding;
import com.example.redditapp.model.Post;
import com.example.redditapp.sync.FetchUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        PostsAdapter.PostsAdapterOnClickHandler {

    private ActivityMainBinding binding;
    private LinearLayoutManager mLayoutManager;
    private PostsAdapter mPostsAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String mCurrentSubreddit;
    private int mCurrentPosition = 0;
    private static final int ID_POSTS_LOADER = 32;
    private AdView mAdView;
    private boolean mLoaderInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        refreshSubreddits();

        binding.leftDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view;
                String subreddit = textView.getText().toString();
                int position = i;
                selectItem(position, subreddit);
                refreshPosts();
            }
        });

        binding.leftDrawer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                String subreddit = textView.getText().toString();
                Uri uri = RedditContract.SubredditEntry.CONTENT_URI;
                getContentResolver().delete(uri, "name=?", new String[]{subreddit});
                refreshSubreddits();
                binding.drawerLayout.closeDrawer(binding.leftDrawer);
                if (subreddit.equals(mCurrentSubreddit)) {
                    mCurrentSubreddit = binding.leftDrawer.getItemAtPosition(0).toString();
                    if (mCurrentSubreddit != null && !subreddit.equals(mCurrentSubreddit)) {
                        selectItem(0, mCurrentSubreddit);
                        refreshPosts();
                    } else {
                        mCurrentSubreddit = null;
                        binding.leftDrawer.setAdapter(new ArrayAdapter<>(MainActivity.this,
                                R.layout.drawer_list_item,
                                new String[0]));
                        mPostsAdapter.clear();
                        mLoaderInitialized = false;
                    }
                }
                View contentView = findViewById(android.R.id.content);
                Snackbar.make(contentView, subreddit + " was deleted.", Snackbar.LENGTH_SHORT)
                        .show();
                return true;
            }
        });

        mTitle = mDrawerTitle = getTitle();
        final ActionBar actionBar = getSupportActionBar();
        mDrawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (actionBar != null) {
                    actionBar.setTitle(mTitle);
                }
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (actionBar != null) {
                    actionBar.setTitle(mTitle);
                }
                invalidateOptionsMenu();
            }
        };
        binding.drawerLayout.addDrawerListener(mDrawerToggle);

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.postsRv.setLayoutManager(mLayoutManager);
        mPostsAdapter = new PostsAdapter(this, this);
        binding.postsRv.setAdapter(mPostsAdapter);

        if (savedInstanceState == null) {
            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            int position = sharedPref.getInt(getString(R.string.position), 0);
            Object item = binding.leftDrawer.getItemAtPosition(position);
            if (item != null) {
                String subreddit = item.toString();
                selectItem(position, subreddit);
                getSupportLoaderManager().initLoader(ID_POSTS_LOADER, null, this);
                mLoaderInitialized = true;
            }
        } else {
            String subreddit = savedInstanceState.getString("subreddit");
            int position = savedInstanceState.getInt("position");
            if (subreddit != null) {
                selectItem(position, subreddit);
                mLoaderInitialized = true;
                refreshPosts();
            }

        }
        FetchUtils.initialize(this);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (mCurrentSubreddit != null) {
            savedInstanceState.putString("subreddit", mCurrentSubreddit);
            savedInstanceState.putInt("position", mCurrentPosition);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action buttons
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshPosts();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectItem(int position, String subreddit) {
        mCurrentPosition = position;
        mCurrentSubreddit = subreddit;
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.position), position);
        editor.putString(getString(R.string.subreddit), subreddit);
        editor.apply();
        binding.leftDrawer.setItemChecked(position, true);
        binding.drawerLayout.closeDrawer(binding.leftDrawer);
    }

    private void refreshSubreddits() {
        ArrayList<String> subreddits = new ArrayList<>();
        Cursor cursor = getContentResolver().query(RedditContract.SubredditEntry.CONTENT_URI,
                null, null, null,
                RedditContract.SubredditEntry.COLUMN_NAME + " COLLATE NOCASE ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                subreddits.add(cursor.getString(cursor.getColumnIndex(RedditContract.SubredditEntry.COLUMN_NAME)));
            } while (cursor.moveToNext());
            cursor.close();
        }
        if (subreddits.size() > 0) {
            binding.leftDrawer.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item,
                    subreddits.toArray(new String[0])));
        }
    }

    private void refreshPosts() {
        if (mLoaderInitialized) {
            getSupportLoaderManager().restartLoader(ID_POSTS_LOADER, null, this);
            FetchUtils.startImmediateSync(this);
        }
    }

    private void addSubreddit() {
        Intent intent = new Intent(MainActivity.this, FollowActivity.class);
        startActivity(intent);
    }

    private void showLoading() {
        binding.pbLoadingIndicator.setVisibility(View.VISIBLE);
        binding.pbLoadingIndicator.getIndeterminateDrawable()
                .setColorFilter(ResourcesCompat.getColor(getResources(), R.color.progressBarColor, null),
                        PorterDuff.Mode.MULTIPLY);
        binding.postsRv.setVisibility(View.INVISIBLE);
    }

    private void showContent() {
        binding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
        binding.postsRv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Post post) {
        Intent intent = new Intent(MainActivity.this, PostDetailActivity.class);
        intent.putExtra("post", post);
        startActivity(intent);
    }

    public void onClickAddImageButton(View view) {
        addSubreddit();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        showLoading();
        switch (id) {
            case ID_POSTS_LOADER:
                Uri QueryUri = RedditContract.PostEntry.CONTENT_URI;
                return new CursorLoader(this,
                        QueryUri,
                        null,
                        "subreddit=?",
                        new String[]{mCurrentSubreddit},
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ArrayList<Post> posts = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Post post = new Post();
                post.setTitle(cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_TITLE)));
                post.setAuthor(cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_AUTHOR)));
                post.setSubreddit(cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_SUBREDDIT)));
                post.setThumbnail(cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_THUMBNAIL)));
                post.setUrl(cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_URL)));
                post.setPermalink(cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_PERMALINK)));
                posts.add(post);
            } while (cursor.moveToNext());
            cursor.close();
            showContent();
            mPostsAdapter.setPosts(posts);
        } else {
            binding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
            View contentView = findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(contentView, getString(R.string.connectivity_error),
                    Snackbar.LENGTH_LONG);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshPosts();
                }
            };
            snackbar.setAction(R.string.retry, onClickListener);
            snackbar.show();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        showLoading();
        mPostsAdapter.setPosts(null);
    }
}
