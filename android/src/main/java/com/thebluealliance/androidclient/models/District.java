package com.thebluealliance.androidclient.models;

import com.thebluealliance.androidclient.database.TbaDatabaseModel;
import com.thebluealliance.androidclient.database.tables.DistrictsTable;

import android.content.ContentValues;

public class District implements TbaDatabaseModel {

    private String key;
    private String abbreviation;
    private int districtEnum;
    private int year;
    private String name;
    private int numEvents;

    public static final String[] NOTIFICATION_TYPES = {
            // NotificationTypes.DISTRICT_POINTS_UPDATED
    };

    public District() {
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public int getEnum() {
        return districtEnum;
    }

    public void setEnum(int districtEnum) {
        this.districtEnum = districtEnum;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumEvents() {
        return numEvents;
    }

    public void setNumEvents(int numEvents) {
        this.numEvents = numEvents;
    }

    @Override
    public ContentValues getParams() {
        ContentValues params = new ContentValues();
        params.put(DistrictsTable.KEY, getKey());
        params.put(DistrictsTable.ABBREV, getAbbreviation());
        params.put(DistrictsTable.ENUM, getEnum());
        params.put(DistrictsTable.YEAR, getYear());
        params.put(DistrictsTable.NAME, getName());
        return params;
    }
}
