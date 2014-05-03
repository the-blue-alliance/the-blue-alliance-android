package com.thebluealliance.androidclient.models;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.datatypes.TeamListElement;

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

    public int getTeamNumber() {
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

    @Override
    public TeamListElement render() {
        return new TeamListElement(teamKey, teamNumber, nickname, location);
    }
}
