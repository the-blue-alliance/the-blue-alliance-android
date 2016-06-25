package com.thebluealliance.androidclient.listeners;

import com.thebluealliance.androidclient.activities.TeamAtDistrictActivity;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.helpers.DistrictTeamHelper;
import com.thebluealliance.androidclient.helpers.TeamHelper;

import android.content.Context;
import android.content.Intent;
import android.view.View;

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
            AnalyticsHelper.sendClickUpdate(c, "team@district_click", DistrictTeamHelper.generateKey(teamKey, districtKey), "");
            c.startActivity(intent);
        }
    }
}
