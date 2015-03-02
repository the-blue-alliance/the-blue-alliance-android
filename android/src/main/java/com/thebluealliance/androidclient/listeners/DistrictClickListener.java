package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.thebluealliance.androidclient.Analytics;
import com.thebluealliance.androidclient.activities.ViewDistrictActivity;
import com.thebluealliance.androidclient.helpers.DistrictHelper;

/**
 * Created by phil on 7/24/14.
 */
public class DistrictClickListener implements View.OnClickListener {

    private Context context;
    private String key;

    public DistrictClickListener(Context context, String key) {
        this.context = context;
        if (DistrictHelper.validateDistrictKey(key)) {
            this.key = key;
        } else {
            throw new IllegalArgumentException("Invalid district key");
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = ViewDistrictActivity.newInstance(context, key);
        Tracker t = Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, context);
        t.send(new HitBuilders.EventBuilder()
                .setCategory("district_click")
                .setAction(intent.getDataString())
                .setLabel(key)
                .build());
        context.startActivity(intent);
    }
}
