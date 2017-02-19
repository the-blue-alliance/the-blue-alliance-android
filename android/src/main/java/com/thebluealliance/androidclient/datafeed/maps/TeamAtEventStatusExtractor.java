package com.thebluealliance.androidclient.datafeed.maps;

import com.thebluealliance.androidclient.models.EventTeam;
import com.thebluealliance.androidclient.models.TeamAtEventStatus;

import rx.functions.Func1;

public class TeamAtEventStatusExtractor implements Func1<EventTeam, TeamAtEventStatus> {
    @Override
    public TeamAtEventStatus call(EventTeam eventTeam) {
        if (eventTeam == null) return null;
        return eventTeam.getStatus();
    }
}
