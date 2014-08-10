package com.thebluealliance.androidclient.intents;

import android.content.Intent;

import com.thebluealliance.androidclient.listitems.EventListElement;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.models.Match;

/**
 * File created by phil on 7/18/14.
 */
public class LiveEventBroadcast extends Intent {

    public static final String ACTION = "com.thebluealliance.androidclient.LIVE_MATCH",
            NEXT_MATCH = "next",
            LAST_MATCH = "last",
            EVENT = "event";

    private LiveEventBroadcast() {
        super();
        setAction(ACTION);
    }

    public LiveEventBroadcast(Match nextMatch, Match lastMatch) {
        this();
        MatchListElement next, last;
        if (nextMatch != null) {
            next = nextMatch.render();
            if (next != null) {
                putExtra(NEXT_MATCH, next);
            }
        }
        if (lastMatch != null) {
            last = lastMatch.render();
            if (last != null) {
                putExtra(LAST_MATCH, lastMatch.render());
            }
        }
    }

    public LiveEventBroadcast(EventListElement event) {
        this();
        if (event != null) {
            putExtra(EVENT, event);
        }
    }
}
