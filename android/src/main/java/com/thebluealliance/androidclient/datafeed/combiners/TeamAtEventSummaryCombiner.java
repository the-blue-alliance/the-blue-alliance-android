package com.thebluealliance.androidclient.datafeed.combiners;

import com.google.gson.JsonArray;

import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.subscribers.TeamAtEventSummarySubscriber;

import javax.inject.Inject;

import rx.functions.Func2;

public class TeamAtEventSummaryCombiner
        implements Func2<JsonArray, Event, TeamAtEventSummarySubscriber.Model> {

    @Inject
    public TeamAtEventSummaryCombiner() {

    }

    @Override
    public TeamAtEventSummarySubscriber.Model call(JsonArray jsonElements, Event event) {
        return new TeamAtEventSummarySubscriber.Model(jsonElements, event);
    }
}
