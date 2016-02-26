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

    @Override
    public boolean equals(Object o) {
        return o != null
                && o instanceof EventStatsEvent
                && (((EventStatsEvent) o).getStatString() == null && getStatString() == null
                    || ((EventStatsEvent) o).getStatString().equals(getStatString()));
    }
}
