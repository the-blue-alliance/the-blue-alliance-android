package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.thebluealliance.androidclient.database.tables.NotificationsTable;

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
    private int systemId;
    private boolean active;

    public StoredNotification() {
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

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public ContentValues getParams() {
        ContentValues params = new ContentValues();
        params.put(NotificationsTable.TYPE, type);
        params.put(NotificationsTable.TITLE, title);
        params.put(NotificationsTable.BODY, body);
        params.put(NotificationsTable.INTENT, intent);
        params.put(NotificationsTable.TIME, time.getTime());
        params.put(NotificationsTable.SYSTEM_ID, systemId);
        return params;
    }
}
