package com.thebluealliance.androidclient;

import android.content.Context;
import android.preference.PreferenceManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

public final class Analytics {

    private Analytics() {
        // unused
    }

    public static final String PROD_ANALYTICS_KEY = "analytics_id";
    private static HashMap<GAnalyticsTracker, Tracker> mTrackers = new HashMap<>();
    private static GoogleAnalytics analytics;
    private static String sAnalyticsId;

    public static void setAnalyticsId(String analyticsId) {
        sAnalyticsId = analyticsId;
        TbaLogger.d("Using analytics ID " + analyticsId);

        // Flush our cached Trackers
        mTrackers.clear();
    }

    public static synchronized Tracker getTracker(GAnalyticsTracker trackerId, Context c) {
        if (!mTrackers.containsKey(trackerId)) {
            if (analytics == null) {
                analytics = GoogleAnalytics.getInstance(c);
                boolean dryRun;

                if (Utilities.isDebuggable()) {
                    dryRun = PreferenceManager.getDefaultSharedPreferences(c).getBoolean("analytics_dry_run", true);
                } else {
                    dryRun = false;
                }
                analytics.setDryRun(dryRun);
                TbaLogger.d("Setting analytics dry run? " + dryRun);
            }

            Tracker t;
            TbaLogger.d("Loaded analytics id: " + sAnalyticsId);
            t = analytics.newTracker(sAnalyticsId);
            t.setAppId(BuildConfig.VERSION_NAME);
            t.setAppName(c.getString(R.string.app_name));
            t.setAppVersion(Utilities.getVersionNumber());
            t.setSessionTimeout(300);
            t.set("ga_logLevel", "verbose");
            t.setSampleRate(100.0);
            t.enableAutoActivityTracking(true);
            t.enableExceptionReporting(true);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    public static void setAnalyticsDryRun(Context c, boolean dryRun) {
        if (analytics == null) {
            analytics = GoogleAnalytics.getInstance(c);
            analytics.setDryRun(dryRun);
            TbaLogger.d("Setting analytics dry run? " + dryRun);
        }
    }

    public enum GAnalyticsTracker {
        ANDROID_TRACKER;    // main tracker. We can add others in the future, if we need
    }
}
