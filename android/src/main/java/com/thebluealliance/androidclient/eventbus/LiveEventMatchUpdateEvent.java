package com.thebluealliance.androidclient.eventbus;

import com.thebluealliance.androidclient.models.Match;

public class LiveEventMatchUpdateEvent {

    private Match lastMatch, nextMatch;

    public LiveEventMatchUpdateEvent(Match lastMatch, Match nextMatch) {
        this.lastMatch = lastMatch;
        this.nextMatch = nextMatch;
    }

    public Match getLastMatch() {
        return lastMatch;
    }

    public Match getNextMatch() {
        return nextMatch;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof LiveEventMatchUpdateEvent)
                && (((LiveEventMatchUpdateEvent) o).lastMatch == lastMatch)
                && (((LiveEventMatchUpdateEvent) o).nextMatch == nextMatch);
    }
}
