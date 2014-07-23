package com.thebluealliance.androidclient;

import android.app.Application;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/**
 * File created by phil on 7/21/14.
 */
public class TBAAndroid extends Application {

    private static final String PROD_ANALYTICS_KEY = "analytics.id";
    private static final String DEBUG_ANALYTICS_KEY = "analytics.id.debug";

    public enum GAnalyticsTracker{
        ANDROID_TRACKER;    // main tracker. We can add others in the future, if we need
    }

    HashMap<GAnalyticsTracker, Tracker> mTrackers = new HashMap<>();

    private GoogleAnalytics analytics;

    public synchronized Tracker getTracker(GAnalyticsTracker trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            if(analytics == null) {
                analytics = GoogleAnalytics.getInstance(this);
                boolean dryRun;

                if (Utilities.isDebuggable(this)) {
                    dryRun = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("analytics_dry_run", true);
                } else {
                    dryRun = false;
                }
                analytics.setDryRun(dryRun);
                Log.d("GAV4", "Setting analytics dry run? " + dryRun);
            }

            String id;
            if(Utilities.isDebuggable(this)){
                boolean useDebugKey = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("analytics_debug_key", true);
                id = Utilities.readLocalProperty(this, useDebugKey ? DEBUG_ANALYTICS_KEY : PROD_ANALYTICS_KEY);
            }else{
                id = Utilities.readLocalProperty(this, PROD_ANALYTICS_KEY);
            }
            Tracker t;
            Log.d("GAV4", "Loaded analytics id: "+id);
            t = analytics.newTracker(id);
            t.enableAutoActivityTracking(true);
            t.enableExceptionReporting(true);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    public void setAnalyticsDryRun(boolean dryRun){
        if(analytics == null) {
            analytics = GoogleAnalytics.getInstance(this);
            analytics.setDryRun(dryRun);
            Log.d("GAV4", "Setting analytics dry run? " + dryRun);
        }
    }
}
