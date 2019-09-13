package com.example.redditapp.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class FetchService extends JobService {
    private AsyncTask<Void, Void, Void> mFetchTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        mFetchTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                FetchTask.fetchPosts(context);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job, false);
            }
        };
        mFetchTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mFetchTask != null) {
            mFetchTask.cancel(true);
        }
        return true;
    }
}
