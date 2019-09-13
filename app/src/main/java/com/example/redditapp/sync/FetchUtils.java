package com.example.redditapp.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.redditapp.data.RedditContract;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class FetchUtils {
    private static final int INTERVAL_HOURS = 3;
    private static final int INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(INTERVAL_HOURS);
    private static final int FLEXTIME_SECONDS = INTERVAL_SECONDS / 3;

    private static boolean sInitialized;

    private static final String FETCH_TAG = "fetch";

    private static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job fetchJob = dispatcher.newJobBuilder()
                .setService(FetchService.class)
                .setTag(FETCH_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        INTERVAL_SECONDS,
                        INTERVAL_SECONDS + FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(fetchJob);
    }

    synchronized public static void initialize(@NonNull final Context context) {

        if (sInitialized) return;

        sInitialized = true;

        scheduleFirebaseJobDispatcherSync(context);

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {


                Uri forecastQueryUri = RedditContract.PostEntry.CONTENT_URI;

                String[] projectionColumns = {RedditContract.PostEntry._ID};

                Cursor cursor = context.getContentResolver().query(
                        forecastQueryUri,
                        projectionColumns,
                        null,
                        null,
                        null);

                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                if (cursor != null) {
                    cursor.close();
                }
            }
        });
        checkForEmpty.start();
    }

    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, FetchIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
