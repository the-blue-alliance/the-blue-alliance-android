package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.thebluealliance.androidclient.helpers.EventHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


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

    public static HashMap<String, ArrayList<SimpleEvent>> groupByWeek(ArrayList<SimpleEvent> events) {
        HashMap<String, ArrayList<SimpleEvent>> groups = new HashMap<>();
        ArrayList<SimpleEvent> offseason = new ArrayList<>(),
                preseason = new ArrayList<>(),
                weekless = new ArrayList<>();

        for (SimpleEvent e : events) {
            ArrayList<SimpleEvent> list;
            if (e.isOfficial() && (e.getEventType() == EventHelper.TYPE.CMP_DIVISION || e.getEventType() == EventHelper.TYPE.CMP_FINALS)) {
                if (!groups.containsKey(EventHelper.CHAMPIONSHIP_LABEL) || groups.get(EventHelper.CHAMPIONSHIP_LABEL) == null) {
                    list = new ArrayList<>();
                    groups.put(EventHelper.CHAMPIONSHIP_LABEL, list);
                } else {
                    list = groups.get(EventHelper.CHAMPIONSHIP_LABEL);
                }
                list.add(e);
            } else if (e.isOfficial() && (e.getEventType() == EventHelper.TYPE.REGIONAL || e.getEventType() == EventHelper.TYPE.DISTRICT || e.getEventType() == EventHelper.TYPE.DISTRICT_CMP)) {
                if (e.getStartDate() == null) {
                    weekless.add(e);
                } else {
                    String label = String.format(EventHelper.REGIONAL_LABEL, e.getCompetitionWeek());
                    if (groups.containsKey(label) && groups.get(label) != null) {
                        groups.get(label).add(e);
                    } else {
                        list = new ArrayList<>();
                        list.add(e);
                        groups.put(label, list);
                    }
                }
            } else if (e.getEventType() == EventHelper.TYPE.PRESEASON) {
                preseason.add(e);
            } else {
                offseason.add(e);
            }
        }

        if (!weekless.isEmpty()) {
            groups.put(EventHelper.WEEKLESS_LABEL, weekless);
        }
        if (!offseason.isEmpty()) {
            groups.put(EventHelper.OFFSEASON_LABEL, offseason);
        }
        if (!preseason.isEmpty()) {
            groups.put(EventHelper.PRESEASON_LABEL, preseason);
        }

        return groups;
    }
}
