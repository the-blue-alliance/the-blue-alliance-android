package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.thebluealliance.androidclient.datafeed.Database;

import java.util.Date;

/**
 * Created by phil on 2/3/15.
 */
public class StoredNotification {
    private int id;
    private String type;
    private String title;
    private String body;
    private String intent;
    private Date time;
    
    public StoredNotification(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
    
    public ContentValues getParams(){
        ContentValues params = new ContentValues();
        params.put(Database.Notifications.ID, id);
        params.put(Database.Notifications.TYPE, type);
        params.put(Database.Notifications.TITLE, title);
        params.put(Database.Notifications.BODY, body);
        params.put(Database.Notifications.INTENT, intent);
        params.put(Database.Notifications.TIME, time.getTime());
        return params;
    }
}
