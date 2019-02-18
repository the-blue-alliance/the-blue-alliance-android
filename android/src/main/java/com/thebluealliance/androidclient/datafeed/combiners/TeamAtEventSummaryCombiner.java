package com.thebluealliance.androidclient.datafeed.combiners;

import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.TeamAtEventStatus;
import com.thebluealliance.androidclient.subscribers.TeamAtEventSummarySubscriber;

import rx.functions.Func2;

public class TeamAtEventSummaryCombiner implements Func2<TeamAtEventStatus, Event, TeamAtEventSummarySubscriber.Model> {
    @Override
    public TeamAtEventSummarySubscriber.Model call(TeamAtEventStatus teamAtEventStatus, Event event) {
        return new TeamAtEventSummarySubscriber.Model(teamAtEventStatus, event);
    }
}
