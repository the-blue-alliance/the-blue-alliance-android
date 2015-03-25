package com.thebluealliance.androidclient.helpers;

import android.content.Context;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.thebluealliance.androidclient.Analytics;

/**
 * Created by phil on 3/24/15.
 */
public class AnalyticsHelper {

    public static boolean ANALYTICS_ENABLED = true;

    public static void sendTimingUpdate(Context c, long time, String name, String label){
        if(!ANALYTICS_ENABLED) return;

        Tracker t = Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, c);

        t.send(new HitBuilders.TimingBuilder()
            .setCategory("load_timing")
            .setValue(time)
            .setVariable(name)
            .setLabel(label)
            .build());
    }

    public static void sendSearchUpdate(Context c, String query){
        if(!ANALYTICS_ENABLED) return;

        Tracker t = Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, c);
        t.send(new HitBuilders.EventBuilder()
                .setCategory("search")
                .setAction(query)
                .setLabel("search")
                .build());
    }

    public static void sendRefreshUpdate(Context c, String key){
        if(!ANALYTICS_ENABLED) return;

        Tracker t = Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, c);
        t.send(new HitBuilders.EventBuilder()
                .setCategory("refresh")
                .setAction("toolbar-button")
                .setLabel(key)
                .build());
    }

    public static void sendClickUpdate(Context c, String category, String action, String key){
        if(!ANALYTICS_ENABLED) return;

        Tracker t = Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, c);
        t.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(key)
                .build());
    }

    public static void sendSocialUpdate(Context c, String network, String key){
        if(!ANALYTICS_ENABLED) return;

        Tracker t = Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, c);
        t.send(new HitBuilders.SocialBuilder()
                .setNetwork(network)
                .setAction("social-click")
                .setTarget(key)
                .build());
    }
}
