package com.thebluealliance.androidclient.eventbus;

/**
 * Created by Nathan on 8/15/2014.
 */
public class YearChangedEvent  {

    private int year;

    public YearChangedEvent(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }
}
