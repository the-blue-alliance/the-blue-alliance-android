package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.thebluealliance.androidclient.datatypes.ListElement;


public class Media implements BasicModel {

    public enum TYPE {
        NONE,
        YOUTUBE,
        CD_PHOTO_THREAD
    }

    Media.TYPE mediaType;
    String foreignKey,
            teamKey;
    JsonElement details;
    int year;
    long last_updated;

    public Media() {
        this.mediaType = TYPE.NONE;
        this.foreignKey = "";
        this.teamKey = "";
        this.details = new JsonNull();
        this.year = -1;
        this.last_updated = -1;
    }

    public Media(TYPE mediaType, String foreignKey, String teamKey, JsonElement details, int year, long last_updated) {
        this.mediaType = mediaType;
        this.foreignKey = foreignKey;
        this.teamKey = teamKey;
        this.details = details;
        this.year = year;
        this.last_updated = last_updated;
    }

    public Media.TYPE getMediaType() {
        return mediaType;
    }

    public void setMediaType(Media.TYPE mediaType) {
        this.mediaType = mediaType;
    }

    public String getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey;
    }

    public String getTeamKey() {
        return teamKey;
    }

    public void setTeamKey(String teamKey) {
        this.teamKey = teamKey;
    }

    public JsonElement getDetails() {
        return details;
    }

    public void setDetails(JsonElement details) {
        this.details = details;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public long getLastUpdated() {
        return last_updated;
    }

    public void setLastUpdated(long last_updated) {
        this.last_updated = last_updated;
    }

    @Override
    public ListElement render() {
        //TODO implement this, eventually
        return null;
    }

    @Override
    public ContentValues getParams() {
        return null;
    }

}
