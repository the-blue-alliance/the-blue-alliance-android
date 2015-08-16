package com.thebluealliance.androidclient.eventbus;

public class EventRankingsEvent {

    public static final int SIZE = 5;

    String mRankString;

    public EventRankingsEvent(String rankString) {
        mRankString = rankString;
    }

    public String getRankString() {
        return mRankString;
    }
}
