package com.thebluealliance.androidclient.models;

public class EventWeekTab {
    private int mWeek;
    private String mLabel;

    public EventWeekTab(int week, String label) {
        mWeek = week;
        mLabel = label;
    }

    public int getWeek() {
        return mWeek;
    }

    public String getLabel() {
        return mLabel;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof EventWeekTab && mWeek == ((EventWeekTab) o).getWeek();
    }
}
