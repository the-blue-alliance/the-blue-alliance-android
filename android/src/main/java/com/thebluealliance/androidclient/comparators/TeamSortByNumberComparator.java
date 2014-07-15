package com.thebluealliance.androidclient.comparators;

import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;

import java.util.Arrays;
import java.util.Comparator;

/**
 * File created by phil on 5/4/14.
 */
public class TeamSortByNumberComparator implements Comparator<Team> {
    @Override
    public int compare(Team team, Team team2) {
        try {
            return team.getTeamNumber().compareTo(team2.getTeamNumber());
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Can't compare teams with missing fields" +
                    Arrays.toString(e.getStackTrace()));
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
