package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.thebluealliance.androidclient.Analytics;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.helpers.TeamHelper;

/**
 * File created by phil on 5/24/14.
 */
public class TeamClickListener implements View.OnClickListener {

    private Context c;

    public TeamClickListener(Context c) {
        super();
        this.c = c;
    }

    @Override
    public void onClick(View v) {
        String teamKey = v.getTag().toString();
        if (TeamHelper.validateTeamKey(teamKey) ^ TeamHelper.validateMultiTeamKey(teamKey)) {
            if (TeamHelper.validateMultiTeamKey(teamKey)) {
                // Take out extra letter at end to make team key valid.
                teamKey = teamKey.substring(0, teamKey.length() - 1);
            }
            /* Track the call */
            Intent intent = ViewTeamActivity.newInstance(c, teamKey);
            Tracker t = Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, c);
            t.send(new HitBuilders.EventBuilder()
                    .setCategory("team_click")
                    .setAction(intent.getDataString())
                    .setLabel(teamKey)
                    .build());
            c.startActivity(intent);
        }
    }
}
