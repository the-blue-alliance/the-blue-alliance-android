package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.datatypes.TeamListElement;

import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;

public class Team implements BasicModel {
    String teamKey,
            nickname,
            location,
            fullName,
            website;
    int teamNumber;
    JsonArray events;
    long last_updated;

    public Team() {
        this.teamKey = "";
        this.nickname = "";
        this.location = "";
        this.teamNumber = -1;
        this.last_updated = -1;
        this.fullName = "";
        this.website = "";
        this.events = new JsonArray();
    }

    public Team(String teamKey, int teamNumber, String fullName, String nickname, String location, String website, JsonArray events, long last_updated) {
        this.teamKey = teamKey;
        this.nickname = nickname;
        this.location = location;
        this.teamNumber = teamNumber;
        this.last_updated = last_updated;
        this.fullName = fullName;
        this.events = events;
        this.website = website;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public JsonArray getEvents() {
        return events;
    }

    public void setEvents(JsonArray events) {
        this.events = events;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTeamKey() {
        return teamKey;
    }

    public void setTeamKey(String teamKey) {
        this.teamKey = teamKey;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getTeamNumber() {
        return teamNumber;
    }

    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    public long getLastUpdated() {
        return last_updated;
    }

    public void setLastUpdated(long last_updated) {
        this.last_updated = last_updated;
    }

    public SimpleEvent getCurrentEvent() {
        Event event = null;
        Date now = new Date(), eventStart, eventEnd;
        Iterator<JsonElement> iterator = events.iterator();
        JsonObject e;
        while (iterator.hasNext()) {
            try {
                e = iterator.next().getAsJsonObject();
                eventStart = Event.eventDateFormat.parse(e.get("start_date").getAsString());
                eventEnd = Event.eventDateFormat.parse(e.get("end_date").getAsString());
                if (now.after(eventStart) && now.before(eventEnd)) {
                    return JSONManager.getGson().fromJson(e, SimpleEvent.class);
                }
            } catch (ParseException ex) {
                //can't parse the date. Give up.
            }
        }
        return null;
    }

    @Override
    public TeamListElement render() {
        return new TeamListElement(teamKey, teamNumber, nickname, location);
    }

    @Override
    public ContentValues getParams() {
        return null;
    }

    public static boolean validateTeamKey(String key) {
        return key.matches("^frc\\d{1,4}$");
    }
}
