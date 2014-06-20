package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.view.View;

import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.helpers.TeamHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (teamKey != null && !teamKey.isEmpty() && TeamHelper.validateTeamKey(teamKey)) {
            c.startActivity(ViewTeamActivity.newInstance(c, teamKey));
        }
        else if (teamKey != null && !teamKey.isEmpty() && !(TeamHelper.validateTeamKey(teamKey))) {
            Pattern pattern = Pattern.compile("^frc\\d{1,4}");
            Matcher matcher = pattern.matcher(teamKey);
            if (matcher.find()) {
                teamKey = matcher.group(0);
                c.startActivity(ViewTeamActivity.newInstance(c, teamKey));
            }
        }
        else {
            throw new IllegalArgumentException("TeamClickListener must be attached to a view with a valid team key set as the tag!");
        }
    }
}
