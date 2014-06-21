package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.thebluealliance.androidclient.helpers.EventHelper;

import java.util.Date;

public class SimpleEvent extends Event implements BasicModel {

    public SimpleEvent() {
        super();
    }

    public SimpleEvent(String eventKey, String eventName, String location, String venue, boolean official, EventHelper.TYPE eventType, int districtEnum, String districtTitle, Date startDate, Date endDate, long last_updated) {
        super();
        if (!EventHelper.validateEventKey(eventKey))
            throw new IllegalArgumentException("Invalid match key. Should be format <year><event>, like 2014cthar");
        this.eventKey = eventKey;
        this.eventYear = Integer.parseInt(eventKey.substring(0, 4));
        this.eventName = eventName;
        this.location = location;
        this.venue = venue;
        this.eventType = eventType;
        this.districtEnum = districtEnum;
        this.districtTitle = districtTitle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.official = official;
        this.last_updated = last_updated;
    }

    @Override
    public ContentValues getParams() {
        return super.getParams();
    }

}
