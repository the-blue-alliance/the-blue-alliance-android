package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.thebluealliance.androidclient.Analytics;
import com.thebluealliance.androidclient.activities.TeamAtDistrictActivity;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.helpers.DistrictTeamHelper;
import com.thebluealliance.androidclient.helpers.TeamHelper;

/**
 * File created by phil on 7/26/14.
 */
public class TeamAtDistrictClickListener implements View.OnClickListener {

    private Context c;
    private String teamKey, districtKey;

    public TeamAtDistrictClickListener(Context c, String teamKey, String districtKey) {
        this.c = c;
        this.teamKey = teamKey;
        this.districtKey = districtKey;
    }

    @Override
    public void onClick(View v) {
        if (TeamHelper.validateTeamKey(teamKey) && DistrictHelper.validateDistrictKey(districtKey)) {
            Intent intent = TeamAtDistrictActivity.newInstance(c, teamKey, districtKey);
            Tracker t = Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, c);
            t.send(new HitBuilders.EventBuilder()
                    .setCategory("team@district_click")
                    .setAction(intent.getDataString())
                    .setLabel(DistrictTeamHelper.generateKey(teamKey, districtKey))
                    .build());
            c.startActivity(intent);
        }
    }
}
