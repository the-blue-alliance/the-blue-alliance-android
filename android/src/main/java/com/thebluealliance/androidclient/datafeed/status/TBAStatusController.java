package com.thebluealliance.androidclient.datafeed.status;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.UpdateRequiredActivity;
import com.thebluealliance.androidclient.background.AnalyticsActions;
import com.thebluealliance.androidclient.models.APIStatus;

import java.util.Calendar;

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

    private final SharedPreferences mPrefs;
    private final Gson mGson;
    private final Tracker mAnalyticsTracker;

    private long mLastUpdateTime;
    private long mLastDialogTime;

    @Inject
    public TBAStatusController(SharedPreferences prefs, Gson gson, Tracker tracker) {
        mPrefs = prefs;
        mGson = gson;
        mAnalyticsTracker = tracker;
        mLastUpdateTime = Long.MIN_VALUE;
        mLastDialogTime = Long.MIN_VALUE;
    }

    public void scheduleStatusUpdate(Context context) {
        context.startService(new Intent(context, StatusRefreshService.class));
    }

    public
    @Nullable
    APIStatus fetchApiStatus() {
        if (!mPrefs.contains(STATUS_PREF_KEY)) {
            return null;
        }

        String statusJson = mPrefs.getString(STATUS_PREF_KEY, "");
        return mGson.fromJson(statusJson, APIStatus.class);
    }

    public int getMaxCompYear() {
        APIStatus status = fetchApiStatus();
        if (status == null) {
            Calendar cal = Calendar.getInstance();
            return cal.get(Calendar.YEAR);
        }
        return status.getMaxSeason();
    }

    public int getMinAppVersion() {
        APIStatus status = fetchApiStatus();
        if (status == null) {
            /* Default to the current version */
            return BuildConfig.VERSION_CODE;
        }
        return status.getMinAppVersion();
    }

    public int getLatestAppVersion() {
        APIStatus status = fetchApiStatus();
        if (status == null) {
            /* Default to the current version */
            return BuildConfig.VERSION_CODE;
        }
        return status.getLatestAppersion();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        new AnalyticsActions.ReportActivityStart(activity).run();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        /* Update myTBA Status */
        if (mLastUpdateTime + UPDATE_TIMEOUT_NS < System.nanoTime()) {
            scheduleStatusUpdate(activity);
            mLastUpdateTime = System.nanoTime();
        }

        if (BuildConfig.VERSION_CODE < getMinAppVersion()) {
            activity.startActivity(new Intent(activity, UpdateRequiredActivity.class));
        } else if (BuildConfig.VERSION_CODE < getLatestAppVersion()
                && mLastDialogTime + UPDATE_TIMEOUT_NS < System.nanoTime()) {
            /* Show an app update dialog */
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.update_dialog_title)
                    .setMessage(R.string.update_dialog_text)
                    .setPositiveButton(R.string.update_dialog_action, (dialog, which) -> {
                  /* Open Play Store page */
                        dialog.dismiss();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.thebluealliance.androidclient"));
                        activity.startActivity(i);
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
            mLastDialogTime = System.nanoTime();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        new AnalyticsActions.ReportActivityStop(activity).run();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
