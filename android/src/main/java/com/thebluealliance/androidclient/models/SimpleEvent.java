package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class SimpleEvent extends Event implements BasicModel {

    public SimpleEvent() {
        super();
    }

    public SimpleEvent(String eventKey, String eventName, String location, boolean official, TYPE eventType, DISTRICT eventDistrict, Date startDate, Date endDate, long last_updated) {
        super();
        if (!Event.validateEventKey(eventKey))
            throw new IllegalArgumentException("Invalid match key. Should be format <year><event>, like 2014cthar");
        this.eventKey = eventKey;
        this.eventName = eventName;
        this.location = location;
        this.eventType = eventType;
        this.eventDistrict = eventDistrict;
        this.startDate = startDate;
        this.endDate = endDate;
        this.official = official;
        this.last_updated = last_updated;
    }

    @Override
    public ContentValues getParams() {
        return super.getParams();
    }

    public static HashMap<String, ArrayList<SimpleEvent>> groupByWeek(ArrayList<SimpleEvent> events){
        HashMap<String, ArrayList<SimpleEvent>> groups = new HashMap<>();
        int currentWeek = 1;
        Date weekStart = null;
        ArrayList<SimpleEvent> offseason = new ArrayList<>(),
                               preseason = new ArrayList<>(),
                               weekless = new ArrayList<>();

        for(SimpleEvent e: events){
            ArrayList<SimpleEvent> list;
            if(e.isOfficial() && (e.getEventType() == TYPE.CMP_DIVISION || e.getEventType() == TYPE.CMP_FINALS)){
                    if(!groups.containsKey(CHAMPIONSHIP_LABEL) || groups.get(CHAMPIONSHIP_LABEL) == null){
                        list = new ArrayList<>();
                        groups.put(CHAMPIONSHIP_LABEL, list);
                    }else{
                        list = groups.get(CHAMPIONSHIP_LABEL);
                    }
                    list.add(e);
            }else if(e.isOfficial() && (e.getEventType() == TYPE.REGIONAL || e.getEventType() == TYPE.DISTRICT || e.getEventType() == TYPE.DISTRICT_CMP)){
                if(e.getStartDate() == null){
                    weekless.add(e);
                }else{
                    String label = String.format(REGIONAL_LABEL, e.getCompetitionWeek());
                    if(groups.containsKey(label) && groups.get(label) != null){
                        groups.get(label).add(e);
                    }else{
                        list = new ArrayList<>();
                        list.add(e);
                        groups.put(label, list);
                    }
                }
            }else if(e.getEventType() == TYPE.PRESEASON){
                preseason.add(e);
            }else{
                offseason.add(e);
            }
        }

        if(weekless.size() > 0){
            groups.put(WEEKLESS_LABEL, weekless);
        }
        if(offseason.size() > 0){
            groups.put(OFFSEASON_LABEL, offseason);
        }
        if(preseason.size() > 0){
            groups.put(PRESEASON_LABEL, preseason);
        }

        return groups;
    }
}
