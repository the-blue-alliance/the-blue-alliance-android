package com.thebluealliance.androidclient.models;

import com.thebluealliance.api.model.IRobot;

import javax.annotation.Nullable;

public class Robot implements IRobot {

    private String key;
    private String name;
    private String teamKey;
    private Integer year;

    @Override public String getKey() {
        return key;
    }

    @Override public void setKey(String key) {
        this.key = key;
    }

    @Override public String getName() {
        return name;
    }

    @Override public void setName(String name) {
        this.name = name;
    }

    @Override public String getTeamKey() {
        return teamKey;
    }

    @Override public void setTeamKey(String teamKey) {
        this.teamKey = teamKey;
    }

    @Override public Integer getYear() {
        return year;
    }

    @Override public void setYear(Integer year) {
        this.year = year;
    }
}
