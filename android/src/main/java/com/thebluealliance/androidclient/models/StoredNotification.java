package com.thebluealliance.androidclient.models;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.database.tables.NotificationsTable;
import com.thebluealliance.androidclient.gcm.notifications.AllianceSelectionNotification;
import com.thebluealliance.androidclient.gcm.notifications.AwardsPostedNotification;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.gcm.notifications.CompLevelStartingNotification;
import com.thebluealliance.androidclient.gcm.notifications.DistrictPointsUpdatedNotification;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.gcm.notifications.ScheduleUpdatedNotification;
import com.thebluealliance.androidclient.gcm.notifications.ScoreNotification;
import com.thebluealliance.androidclient.gcm.notifications.UpcomingMatchNotification;

import java.util.Date;

/**
 * A "Recent Notification" stored locally in the db to show later
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
    private String messageData;

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

    public String getMessageData() {
        return messageData;
    }

    public void setMessageData(String messageData) {
        this.messageData = messageData;
    }

    public ContentValues getParams() {
        ContentValues params = new ContentValues();
        params.put(NotificationsTable.TYPE, type);
        params.put(NotificationsTable.TITLE, title);
        params.put(NotificationsTable.BODY, body);
        params.put(NotificationsTable.INTENT, intent);
        params.put(NotificationsTable.TIME, time.getTime());
        params.put(NotificationsTable.SYSTEM_ID, systemId);
        params.put(NotificationsTable.MSG_DATA, messageData);
        return params;
    }

    /**
     * Gets the related renderable notification
     * You can immediately call {@link BaseNotification#getView(Context, LayoutInflater, View)}
     *
     * @return Appropriate BaseNotification
     */
    public
    @Nullable
    BaseNotification getNotification(DatabaseWriter writer) {
        BaseNotification notification;
        String data = getMessageData();
        switch (getType()) {
            case NotificationTypes.MATCH_SCORE:
                notification = new ScoreNotification(data, writer.getMatchWriter().get());
                break;
            case NotificationTypes.UPCOMING_MATCH:
                notification = new UpcomingMatchNotification(data);
                break;
            case NotificationTypes.ALLIANCE_SELECTION:
                notification = new AllianceSelectionNotification(data, writer.getEventWriter().get());
                break;
            case NotificationTypes.LEVEL_STARTING:
                notification = new CompLevelStartingNotification(data);
                break;
            case NotificationTypes.SCHEDULE_UPDATED:
                notification = new ScheduleUpdatedNotification(data);
                break;
            case NotificationTypes.AWARDS:
                notification = new AwardsPostedNotification(data, writer.getAwardListWriter().get());
                break;
            case NotificationTypes.DISTRICT_POINTS_UPDATED:
                notification = new DistrictPointsUpdatedNotification(data);
                break;
            default:
                return null;
        }
        notification.setDate(getTime());
        return notification;
    }
}
