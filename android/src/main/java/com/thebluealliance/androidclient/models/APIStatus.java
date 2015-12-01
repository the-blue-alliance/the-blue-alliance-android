package com.thebluealliance.androidclient.models;

import java.util.List;

public class APIStatus {
    private int maxSeason;
    private boolean fmsApiDown;
    private int minAppVersion;
    private List<String> downEvents;
    private String jsonBlob;

    public APIStatus() {

    }

    public int getMaxSeason() {
        return maxSeason;
    }

    public void setMaxSeason(int maxSeason) {
        this.maxSeason = maxSeason;
    }

    public boolean isFmsApiDown() {
        return fmsApiDown;
    }

    public void setFmsApiDown(boolean fmsApiDown) {
        this.fmsApiDown = fmsApiDown;
    }

    public int getMinAppVersion() {
        return minAppVersion;
    }

    public void setMinAppVersion(int minAppVersion) {
        this.minAppVersion = minAppVersion;
    }

    public List<String> getDownEvents() {
        return downEvents;
    }

    public void setDownEvents(List<String> downEvents) {
        this.downEvents = downEvents;
    }

    public String getJsonBlob() {
        return jsonBlob;
    }

    public void setJsonBlob(String jsonBlob) {
        this.jsonBlob = jsonBlob;
    }
}
