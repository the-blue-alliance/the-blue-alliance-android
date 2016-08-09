package com.thebluealliance.androidclient.models;

import java.util.Date;

public class APIStatus extends com.thebluealliance.api.model.ApiStatus {
    private long lastOkHttpCacheClear;
    private String jsonBlob;
    private long champsPitLocationsUpdateTime;

    /* Admin Message */
    private Date messageExipration;

    public APIStatus() {

    }

    public Boolean isFmsApiDown() {
        return getFmsApiDown();
    }

    public String getJsonBlob() {
        return jsonBlob;
    }

    public void setJsonBlob(String jsonBlob) {
        this.jsonBlob = jsonBlob;
    }

    public long getChampsPitLocationsUpdateTime() {
        return champsPitLocationsUpdateTime;
    }

    public void setChampsPitLocationsUpdateTime(long time) {
        this.champsPitLocationsUpdateTime = time;
    }

    public Boolean hasMessage() {
        return super.getHasMessage();
    }

    public Date getMessageExipration() {
        return messageExipration;
    }

    public void setMessageExipration(Date messageExipration) {
        this.messageExipration = messageExipration;
    }

    public long getLastOkHttpCacheClear() {
        return lastOkHttpCacheClear;
    }

    public void setLastOkHttpCacheClear(long lastOkHttpCacheClear) {
        this.lastOkHttpCacheClear = lastOkHttpCacheClear;
    }
}
