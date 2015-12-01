package com.thebluealliance.androidclient.datafeed.status;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.models.APIStatus;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * A class to handle the TBA Status API
 * Can fire off {@link StatusRefreshService} to load new data
 */
@Singleton
public class TBAStatusController implements Application.ActivityLifecycleCallbacks {

    public static final String STATUS_PREF_KEY = "tba_status";

    /* 15 minutes in nanoseconds */
    private static final double UPDATE_TIMEOUT_NS = 9e+11;

    private SharedPreferences mPrefs;
    private Gson mGson;

    long mLastUpdateTime;

    @Inject
    public TBAStatusController(SharedPreferences prefs, Gson gson) {
        mPrefs = prefs;
        mGson = gson;
        mLastUpdateTime = -1;
    }

    public void scheduleStatusUpdate(Context context) {
        context.startService(new Intent(context, StatusRefreshService.class));
    }

    public @Nullable APIStatus fetchApiStatus() {
        if (!mPrefs.contains(STATUS_PREF_KEY)) {
            return null;
        }

        String statusJson = mPrefs.getString(STATUS_PREF_KEY, "");
        return mGson.fromJson(statusJson, APIStatus.class);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        /* Update myTBA Status */
        if (mLastUpdateTime + UPDATE_TIMEOUT_NS < System.nanoTime()) {
            scheduleStatusUpdate(activity);
        }
        mLastUpdateTime = System.nanoTime();
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
