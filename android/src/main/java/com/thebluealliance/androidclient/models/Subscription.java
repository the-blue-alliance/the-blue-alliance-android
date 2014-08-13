package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.thebluealliance.androidclient.datafeed.Database;

/**
 * File created by phil on 8/13/14.
 */
public class Subscription {
    private String userName;
    private String modelKey;

    public Subscription(){

    }

    public Subscription(String userName, String modelKey) {
        this.userName = userName;
        this.modelKey = modelKey;
    }

    public String getKey(){
        return userName+":"+modelKey;
    }

    public String getUserName() {
        return userName;
    }

    public String getModelKey() {
        return modelKey;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setModelKey(String modelKey) {
        this.modelKey = modelKey;
    }

    public ContentValues getParams(){
        ContentValues cv = new ContentValues();
        cv.put(Database.Subscriptions.KEY, getKey());
        cv.put(Database.Subscriptions.USER_NAME, userName);
        cv.put(Database.Subscriptions.MODEL_KEY, modelKey);
        return cv;
    }
}
