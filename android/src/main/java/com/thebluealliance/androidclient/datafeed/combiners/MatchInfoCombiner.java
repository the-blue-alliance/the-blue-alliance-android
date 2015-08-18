package com.thebluealliance.androidclient.datafeed.combiners;

import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.subscribers.MatchInfoSubscriber.Model;

import rx.functions.Func2;

public class MatchInfoCombiner implements Func2<Match, Event, Model> {
    @Override
    public Model call(Match match, Event event) {
        return new Model(match, event);
    }
}
