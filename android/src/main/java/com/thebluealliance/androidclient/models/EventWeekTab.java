package com.thebluealliance.androidclient.models;

import java.util.ArrayList;

public class EventWeekTab {

    private String mLabel;
    private int mStartWeek = Integer.MAX_VALUE;

    private int mEndWeek = Integer.MIN_VALUE;

    private ArrayList<String> mEventKeys;

    public EventWeekTab(String label) {
        mLabel = label;
        mEventKeys = new ArrayList<>();
    }

    public String getLabel() {
        return mLabel;
    }

    public boolean includesWeek(int week) {
        return week >= mStartWeek && week <= mEndWeek;
    }

    public ArrayList<String> getEventKeys() {
        return mEventKeys;
    }

    public void addEvent(Event event) {
        mEventKeys.add(event.getKey());

        int eventWeek = event.getWeek() != null ? event.getWeek() : -1;
        if (mStartWeek > eventWeek) {
            mStartWeek = eventWeek;
        }
        if (mEndWeek < eventWeek) {
            mEndWeek = eventWeek;
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof EventWeekTab && mLabel == ((EventWeekTab) o).getLabel();
    }
}
