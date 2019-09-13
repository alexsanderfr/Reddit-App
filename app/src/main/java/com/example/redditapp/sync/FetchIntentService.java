package com.example.redditapp.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class FetchIntentService extends IntentService {
    public FetchIntentService() {
        super("FetchIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        FetchTask.fetchPosts(this);
    }
}
