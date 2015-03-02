package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.thebluealliance.androidclient.Analytics;
import com.thebluealliance.androidclient.helpers.ModelHelper;

/**
 * File created by phil on 8/13/14.
 */
public class ModelClickListener implements View.OnClickListener{

    private Context context;
    private String key;
    private ModelHelper.MODELS type;

    public ModelClickListener(Context context, String key, ModelHelper.MODELS type){
        this.key = key;
        this.context = context;
        this.type = type;
    }

    @Override
    public void onClick(View v) {
        Intent intent = ModelHelper.getIntentFromKey(context, key, type);
        Tracker t = Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, context);
        t.send(new HitBuilders.EventBuilder()
                .setCategory("model_click")
                .setAction(intent.getDataString())
                .setLabel(key)
                .build());
        context.startActivity(intent);
    }
}
