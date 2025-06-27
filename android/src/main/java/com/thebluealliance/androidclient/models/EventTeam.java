package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.TbaDatabaseModel;
import com.thebluealliance.androidclient.database.tables.EventTeamsTable;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;

import javax.annotation.Nullable;

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
    private @Nullable TeamAtEventStatus status;

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

    @Nullable public TeamAtEventStatus getStatus() {
        return status;
    }

    public void setStatus(@Nullable TeamAtEventStatus status) {
        this.status = status;
    }

    @Override
    public ContentValues getParams(Gson gson) {
        ContentValues params = new ContentValues();
        params.put(EventTeamsTable.KEY, getKey());
        params.put(EventTeamsTable.TEAMKEY, getTeamKey());
        params.put(EventTeamsTable.EVENTKEY, getEventKey());
        params.put(EventTeamsTable.YEAR, getYear());
        params.put(EventTeamsTable.STATUS, status != null ? gson.toJson(status, TeamAtEventStatus.class) : "");
        return params;
    }
}
