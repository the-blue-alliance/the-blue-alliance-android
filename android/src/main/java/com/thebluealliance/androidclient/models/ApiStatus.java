package com.thebluealliance.androidclient.models;

import com.thebluealliance.api.model.IApiStatus;

import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class ApiStatus implements IApiStatus {

    private Integer currentSeason;
    private List<String> downEvents;
    private Boolean fmsApiDown;
    private Boolean hasMessage;
    private Long lastModified;
    private Long lastOkHttpCacheClear;
    private Integer latestAppVersion;
    private Integer maxSeason;
    private Long messageExpiration;
    private String messageText;
    private Integer minAppVersion;
    private String jsonBlob;

    /* Admin Message */
    private Date messageExipration;

    public ApiStatus() {

    }

    @Nullable @Override public Integer getCurrentSeason() {
        return currentSeason;
    }

    @Override public void setCurrentSeason(Integer currentSeason) {
        this.currentSeason = currentSeason;
    }

    @Nullable @Override public List<String> getDownEvents() {
        return downEvents;
    }

    @Override public void setDownEvents(List<String> downEvents) {
        this.downEvents = downEvents;
    }

    @Nullable @Override public Boolean getFmsApiDown() {
        return fmsApiDown;
    }

    @Override public void setFmsApiDown(Boolean fmsApiDown) {
        this.fmsApiDown = fmsApiDown;
    }

    @Nullable @Override public Boolean getHasMessage() {
        return hasMessage;
    }

    @Override public void setHasMessage(Boolean hasMessage) {
        this.hasMessage = hasMessage;
    }

    @Nullable @Override public Long getLastModified() {
        return lastModified;
    }

    @Override public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    @Nullable @Override public Long getLastOkHttpCacheClear() {
        return lastOkHttpCacheClear;
    }

    @Override public void setLastOkHttpCacheClear(Long lastOkHttpCacheClear) {
        this.lastOkHttpCacheClear = lastOkHttpCacheClear;
    }

    @Nullable @Override public Integer getLatestAppVersion() {
        return latestAppVersion;
    }

    @Override public void setLatestAppVersion(Integer latestAppVersion) {
        this.latestAppVersion = latestAppVersion;
    }

    @Nullable @Override public Integer getMaxSeason() {
        return maxSeason;
    }

    @Override public void setMaxSeason(Integer maxSeason) {
        this.maxSeason = maxSeason;
    }

    @Nullable @Override public Long getMessageExpiration() {
        return messageExpiration;
    }

    @Override public void setMessageExpiration(Long messageExpiration) {
        this.messageExpiration = messageExpiration;
    }

    @Nullable @Override public String getMessageText() {
        return messageText;
    }

    @Override public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    @Nullable @Override public Integer getMinAppVersion() {
        return minAppVersion;
    }

    @Override public void setMinAppVersion(Integer minAppVersion) {
        this.minAppVersion = minAppVersion;
    }

    public Date getMessageExipration() {
        return messageExipration;
    }

    public void setMessageExipration(Date messageExipration) {
        this.messageExipration = messageExipration;
    }

    public String getJsonBlob() {
        return jsonBlob;
    }

    public void setJsonBlob(String jsonBlob) {
        this.jsonBlob = jsonBlob;
    }

}
