package com.thebluealliance.androidclient.models;

import com.thebluealliance.androidclient.database.TbaDatabaseModel;
import com.thebluealliance.androidclient.database.tables.EventTeamsTable;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;

import android.content.ContentValues;

public class EventTeam implements TbaDatabaseModel {

    public static final String[] NOTIFICATION_TYPES = {
            NotificationTypes.UPCOMING_MATCH,
            NotificationTypes.MATCH_SCORE,
            NotificationTypes.ALLIANCE_SELECTION,
            //NotificationTypes.AWARDS,
            //NotificationTypes.FINAL_RESULTS
    };

    private String key;
    private String teamKey;
    private String eventKey;
    private Integer year;
    private Integer compWeek;

    public EventTeam() {
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTeamKey() {
        return teamKey;
    }

    public void setTeamKey(String teamKey) {
        this.teamKey = teamKey;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getCompWeek() {
        return compWeek;
    }

    public void setCompWeek(Integer compWeek) {
        this.compWeek = compWeek;
    }

    @Override
    public ContentValues getParams() {
        ContentValues params = new ContentValues();
        params.put(EventTeamsTable.KEY, getKey());
        params.put(EventTeamsTable.TEAMKEY, getTeamKey());
        params.put(EventTeamsTable.EVENTKEY, getEventKey());
        params.put(EventTeamsTable.YEAR, getYear());
        params.put(EventTeamsTable.COMPWEEK, getCompWeek());
        return params;
    }
}
