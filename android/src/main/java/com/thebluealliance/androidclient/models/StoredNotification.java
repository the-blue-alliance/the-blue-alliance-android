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
    private String systemId;
    private boolean active;
    
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

    public boolean isActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = (active == 1);
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public ContentValues getParams(){
        ContentValues params = new ContentValues();
        params.put(Database.Notifications.TYPE, type);
        params.put(Database.Notifications.TITLE, title);
        params.put(Database.Notifications.BODY, body);
        params.put(Database.Notifications.INTENT, intent);
        params.put(Database.Notifications.TIME, time.getTime());
        params.put(Database.Notifications.SYSTEM_ID, systemId);
        params.put(Database.Notifications.ACTIVE, active?1:0);
        return params;
    }
}
