package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datatypes.ImageListElement;
import com.thebluealliance.androidclient.datatypes.ListElement;


public class Media implements BasicModel {

    public enum TYPE {
        NONE,
        YOUTUBE,
        CD_PHOTO_THREAD;

        public static TYPE fromString(String string){
            switch(string){
                case "cdphotothread":
                    return CD_PHOTO_THREAD;
                case "youtube":
                    return YOUTUBE;
                default:
                    return NONE;
            }
        }
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

    public void setMediaType(String typeString){
        mediaType = TYPE.fromString(typeString);
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
        String imageUrl;
        if(mediaType == TYPE.CD_PHOTO_THREAD){
            imageUrl = String.format(Constants.MEDIA_IMG_URL_PATTERN.get(mediaType), details.getAsJsonObject().get("image_partial").getAsString().replace("_l.jpg","_m.jpg"));
        }else{
            imageUrl = String.format(Constants.MEDIA_IMG_URL_PATTERN.get(mediaType), foreignKey);
        }
        return new ImageListElement(imageUrl,
                String.format(Constants.MEDIA_LINK_URL_PATTERN.get(mediaType), foreignKey));
    }

    @Override
    public ContentValues getParams() {
        return null;
    }

}
