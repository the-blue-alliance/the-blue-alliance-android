package com.thebluealliance.androidclient.listeners;

import com.thebluealliance.androidclient.activities.ViewDistrictActivity;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.helpers.DistrictHelper;

import android.content.Context;
import android.content.Intent;
import android.view.View;

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
        AnalyticsHelper.sendClickUpdate(context, "district_click", key, "");
        context.startActivity(intent);
    }
}
