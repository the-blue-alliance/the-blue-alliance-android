package com.thebluealliance.androidclient.models;

import java.util.ArrayList;

public class EventWeekTab {

    private String mLabel;
    private int mWeek;
    private ArrayList<String> mEventKeys;

    public EventWeekTab(String label, int week) {
        mLabel = label;
        mWeek = week;
        mEventKeys = new ArrayList<>();
    }

    public String getLabel() {
        return mLabel;
    }

    public int getWeek() {
        return mWeek;
    }

    public ArrayList<String> getEventKeys() {
        return mEventKeys;
    }

    public void addEventKey(String eventKey) {
        mEventKeys.add(eventKey);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof EventWeekTab && mLabel == ((EventWeekTab) o).getLabel();
    }
}
