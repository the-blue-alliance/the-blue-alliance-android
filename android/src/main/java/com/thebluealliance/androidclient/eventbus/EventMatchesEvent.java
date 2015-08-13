package com.thebluealliance.androidclient.eventbus;

import com.thebluealliance.androidclient.models.Match;

import java.util.List;

public class EventMatchesEvent {
    private List<Match> mMatches;

    public EventMatchesEvent(List<Match> matches) {
        mMatches = matches;
    }

    public List<Match> getMatches() {
        return mMatches;
    }
}
