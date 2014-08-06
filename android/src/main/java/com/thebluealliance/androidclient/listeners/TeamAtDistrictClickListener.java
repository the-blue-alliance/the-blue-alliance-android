package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.view.View;

import com.thebluealliance.androidclient.activities.TeamAtDistrictActivity;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.models.District;

/**
 * File created by phil on 7/26/14.
 */
public class TeamAtDistrictClickListener implements View.OnClickListener {

    private Context c;
    private String teamKey, districtKey;

    public TeamAtDistrictClickListener(Context c, String teamKey, String districtKey){
        this.c = c;
        this.teamKey = teamKey;
        this.districtKey = districtKey;
    }

    @Override
    public void onClick(View v) {
        if(TeamHelper.validateTeamKey(teamKey) && DistrictHelper.validateDistrictKey(districtKey)) {
            c.startActivity(TeamAtDistrictActivity.newInstance(c, teamKey, districtKey));
        }
    }
}
