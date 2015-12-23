package com.thebluealliance.androidclient.models;

import java.util.Date;
import java.util.List;

public class APIStatus {
    private int maxSeason;
    private boolean fmsApiDown;
    private int minAppVersion;
    private int latestAppersion;
    private List<String> downEvents;
    private String jsonBlob;

    /* Admin Message */
    private boolean hasMessage;
    private String messageText;
    private Date messageExipration;

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

    public int getLatestAppersion() {
        return latestAppersion;
    }

    public void setLatestAppersion(int latestAppersion) {
        this.latestAppersion = latestAppersion;
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

    public boolean hasMessage() {
        return hasMessage;
    }

    public void setHasMessage(boolean hasMessage) {
        this.hasMessage = hasMessage;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Date getMessageExipration() {
        return messageExipration;
    }

    public void setMessageExipration(Date messageExipration) {
        this.messageExipration = messageExipration;
    }
}
