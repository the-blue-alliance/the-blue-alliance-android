package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.content.Context;

/**
 * Created by Nathan on 7/24/2014.
 */
public abstract class BaseNotification {

    String messageData;
    String messageType;

    public BaseNotification(String messageType, String messageData) {
        this.messageType = messageType;
        this.messageData = messageData;
    }

    public abstract Notification buildNotification(Context context);

    public String getNotificationType() {
        return messageType;
    }

    public abstract int getNotificationId();

}
