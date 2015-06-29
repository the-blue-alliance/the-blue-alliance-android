package com.thebluealliance.androidclient.eventbus;

public class EventStatsEvent {
    public static final int SIZE = 5;

    private String mTopStatString;

    public EventStatsEvent(String topStatString) {
        mTopStatString = topStatString;
    }

    public String getStatString() {
        return mTopStatString;
    }
}
