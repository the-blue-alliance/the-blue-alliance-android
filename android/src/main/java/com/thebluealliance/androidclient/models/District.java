package com.thebluealliance.androidclient.models;

import com.google.gson.Gson;

import com.thebluealliance.androidclient.database.TbaDatabaseModel;
import com.thebluealliance.androidclient.database.tables.DistrictsTable;
import com.thebluealliance.api.model.IDistrict;

import android.content.ContentValues;

import javax.annotation.Nullable;

public class District implements IDistrict, TbaDatabaseModel {

    private String key;
    private String abbreviation;
    private Integer year;
    private String displayName;
    private int numEvents;
    private Long lastModified;

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

    @Nullable @Override
    public Long getLastModified() {
        return lastModified;
    }

    @Override
    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getNumEvents() {
        return numEvents;
    }

    public void setNumEvents(int numEvents) {
        this.numEvents = numEvents;
    }

    @Override
    public ContentValues getParams(Gson gson) {
        ContentValues params = new ContentValues();
        params.put(DistrictsTable.KEY, getKey());
        params.put(DistrictsTable.ABBREV, getAbbreviation());
        params.put(DistrictsTable.YEAR, getYear());
        params.put(DistrictsTable.NAME, getDisplayName());
        params.put(DistrictsTable.LAST_MODIFIED, getLastModified());
        return params;
    }
}
