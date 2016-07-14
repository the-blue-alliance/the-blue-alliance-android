package com.thebluealliance.androidclient.helpers;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import com.thebluealliance.androidclient.Analytics;

import android.content.Context;
import com.thebluealliance.androidclient.Log;

import java.util.Map;

public final class AnalyticsHelper {

    private AnalyticsHelper() {
        // unused
    }

    public static boolean ANALYTICS_ENABLED = true;

    public static void sendTimingUpdate(Context c, long time, String name, String label) {
        if (!ANALYTICS_ENABLED) return;

        Tracker t = Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, c);

        t.send(getTimingHit(time, name, label));
    }

    public static Map<String, String> getTimingHit(long time, String name, String label) {
        return new HitBuilders.TimingBuilder()
                .setCategory("load_timing")
                .setValue(time)
                .setVariable(name)
                .setLabel(label)
                .build();
    }

    public static void sendSearchUpdate(Context c, String query) {
        if (!ANALYTICS_ENABLED) return;

        Tracker t = Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, c);
        t.send(new HitBuilders.EventBuilder()
                .setCategory("search")
                .setAction(query)
                .setLabel("search")
                .build());
    }

    public static Map<String, String> getRefreshHit(String key) {
        return new HitBuilders.EventBuilder()
          .setCategory("refresh")
          .setAction("toolbar-button")
          .setLabel(key)
          .build();
    }

    public static void sendClickUpdate(Context c, String category, String action, String key) {
        if (!ANALYTICS_ENABLED) return;

        Tracker t = Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, c);
        t.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(key)
                .build());
    }

    public static void sendSocialUpdate(Context c, String network, String key) {
        if (!ANALYTICS_ENABLED) return;

        Tracker t = Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, c);
        t.send(new HitBuilders.SocialBuilder()
                .setNetwork(network)
                .setAction("social-click")
                .setTarget(key)
                .build());
    }

    public static Map<String, String> getErrorHit(Throwable throwable) {
        return new HitBuilders.ExceptionBuilder()
          .setDescription(Log.getStackTraceString(throwable))
          .setFatal(false)
          .build();
    }
}
