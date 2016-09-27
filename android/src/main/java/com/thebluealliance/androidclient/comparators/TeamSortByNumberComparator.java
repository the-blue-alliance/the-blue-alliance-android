package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.models.Team;

import java.util.Comparator;

public class TeamSortByNumberComparator implements Comparator<Team> {
    @Override
    public int compare(Team team, Team team2) {
        return team.getTeamNumber().compareTo(team2.getTeamNumber());
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
