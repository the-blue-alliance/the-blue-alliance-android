package com.thebluealliance.androidclient.datafeed.combiners;

import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.models.TeamAtEventStatus;
import com.thebluealliance.androidclient.subscribers.TeamAtEventSummarySubscriber;

import rx.functions.Func3;

public class TeamAtEventSummaryCombiner implements Func3<TeamAtEventStatus, Event, Team, TeamAtEventSummarySubscriber.Model> {
    @Override
    public TeamAtEventSummarySubscriber.Model call(TeamAtEventStatus teamAtEventStatus, Event event, Team team) {
        return new TeamAtEventSummarySubscriber.Model(teamAtEventStatus, event, team);
    }
}
