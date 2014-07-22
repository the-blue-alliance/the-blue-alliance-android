package com.thebluealliance.androidclient;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/**
 * File created by phil on 7/21/14.
 */
public class TBAAndroid extends Application {

    public enum GAnalyticsTracker{
        ANDROID_TRACKER;    // main tracker. We can add others in the future, if we need
    }

    HashMap<GAnalyticsTracker, Tracker> mTrackers = new HashMap<>();

    public synchronized Tracker getTracker(GAnalyticsTracker trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t;
            String id = Utilities.readLocalProperty(this, "analyticsId");
            t = analytics.newTracker(id);
            t.enableAutoActivityTracking(true);
            t.enableExceptionReporting(true);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }
}
