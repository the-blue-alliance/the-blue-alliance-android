package com.thebluealliance.androidclient.eventbus;

public class YearChangedEvent {

    private int year;

    public YearChangedEvent(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }
}
