package com.thebluealliance.androidclient.listeners;

import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.TeamHelper;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public class TeamAtEventClickListener implements View.OnClickListener {

    private Context c;
    private String key;

    public TeamAtEventClickListener(Context c) {
        super();
        this.c = c;
        this.key = null;
    }

    public TeamAtEventClickListener(Context c, String key) {
        super();
        this.c = c;
        this.key = key;
    }

    @Override
    public void onClick(View v) {
        String teamKey, eventKey;
        if (key == null) {
            String tag = v.getTag().toString();
            if (tag.contains("@")) {
                teamKey = tag.split("@")[0];
                eventKey = tag.split("@")[1];
            } else {
                teamKey = tag;
                eventKey = "";
            }
        } else {
            teamKey = key.split("_")[1];
            eventKey = key.split("_")[0];
        }
        if (TeamHelper.validateTeamKey(teamKey) ^ TeamHelper.validateMultiTeamKey(teamKey)) {
            teamKey = TeamHelper.baseTeamKey(teamKey);
            //social button was clicked. Track the call
            Intent intent;
            if (EventHelper.validateEventKey(eventKey)) {
                intent = TeamAtEventActivity.newInstance(c, eventKey, teamKey);
            } else {
                intent = ViewTeamActivity.newInstance(c, teamKey);
            }
            AnalyticsHelper.sendClickUpdate(c, "team@event_click", key, "");
            c.startActivity(intent);
        }
    }
}
