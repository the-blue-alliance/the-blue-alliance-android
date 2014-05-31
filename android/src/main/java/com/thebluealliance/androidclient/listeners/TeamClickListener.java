package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.view.View;

import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.models.Team;

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
        if (teamKey != null && !teamKey.isEmpty() && Team.validateTeamKey(teamKey)) {
            c.startActivity(ViewTeamActivity.newInstance(c, teamKey));
        }
    }
}
