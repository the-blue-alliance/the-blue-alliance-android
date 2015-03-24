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

}
