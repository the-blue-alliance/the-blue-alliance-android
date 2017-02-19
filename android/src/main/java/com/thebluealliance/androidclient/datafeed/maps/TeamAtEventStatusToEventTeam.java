package com.thebluealliance.androidclient.datafeed.maps;

import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.models.EventTeam;
import com.thebluealliance.androidclient.models.TeamAtEventStatus;

import rx.functions.Func1;

public class TeamAtEventStatusToEventTeam implements Func1<TeamAtEventStatus, EventTeam> {

    private final String teamKey;
    private final String eventKey;

    public TeamAtEventStatusToEventTeam(String teamKey, String eventKey) {
        this.eventKey = eventKey;
        this.teamKey = teamKey;
    }

    @Override
    public EventTeam call(TeamAtEventStatus teamAtEventStatus) {
        if (teamAtEventStatus == null) return null;
        EventTeam eventTeam = new EventTeam();
        eventTeam.setKey(EventTeamHelper.generateKey(eventKey, teamKey));
        eventTeam.setYear(EventHelper.getYear(eventKey));
        eventTeam.setTeamKey(teamKey);
        eventTeam.setEventKey(eventKey);
        eventTeam.setLastModified(teamAtEventStatus.getLastModified());
        return eventTeam;
    }
}
