package com.thebluealliance.androidclient.datafeed.combiners;

import com.thebluealliance.androidclient.database.writers.EventTeamAndTeamListWriter.EventTeamAndTeam;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.models.EventTeam;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.functions.Func1;

public class TeamAndEventTeamCombiner implements Func1<List<Team>, EventTeamAndTeam> {

    private String mEventKey;

    @Inject
    public TeamAndEventTeamCombiner(String eventKey) {
        mEventKey = eventKey;
    }

    @Override
    public EventTeamAndTeam call(List<Team> teams) {
        int year = EventHelper.getYear(mEventKey);
        List<EventTeam> eventTeams = new ArrayList<>();
        if (teams == null) {
            return null;
        }
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            EventTeam eventTeam = new EventTeam();
            eventTeam.setYear(year);
            eventTeam.setTeamKey(team.getKey());
            eventTeam.setEventKey(mEventKey);
            eventTeam.setKey(EventTeamHelper.generateKey(mEventKey, team.getKey()));
            eventTeams.add(eventTeam);
        }
        return new EventTeamAndTeam(eventTeams, teams);
    }
}
