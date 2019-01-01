package com.thebluealliance.androidclient.datafeed.combiners;

import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.subscribers.StatsListSubscriber;

import rx.functions.Func2;

public class TwoJsonCombiner implements Func2<JsonElement, JsonElement, StatsListSubscriber.Model> {
    @Override
    public StatsListSubscriber.Model call(JsonElement teamStats, JsonElement eventStats) {
        return new StatsListSubscriber.Model(teamStats, eventStats);
    }
}
