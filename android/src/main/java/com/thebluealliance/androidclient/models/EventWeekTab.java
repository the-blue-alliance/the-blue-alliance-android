package com.thebluealliance.androidclient.models;

public class EventWeekTab {

    private int mWeek;
    private int mMonth;
    private String mLabel;

    public EventWeekTab(int week, int month, String label) {
        mWeek = week;
        mMonth = month;
        mLabel = label;
    }

    public int getWeek() {
        return mWeek;
    }

    public int getMonth() {
        return mMonth;
    }

    public String getLabel() {
        return mLabel;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof EventWeekTab && mWeek == ((EventWeekTab) o).getWeek();
    }
}
