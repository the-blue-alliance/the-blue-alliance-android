package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.JSONManager;

/**
 * Created by Nathan on 7/24/2014.
 */
public abstract class BaseNotification {

    String messageData;
    String messageType;
    Gson gson;
    private String logTag;

    public BaseNotification(String messageType, String messageData) {
        this.messageType = messageType;
        this.messageData = messageData;
        this.gson = JSONManager.getGson();
        this.logTag = null;
    }

    public abstract Notification buildNotification(Context context);

    public String getNotificationType() {
        return messageType;
    }

    public abstract void parseMessageData() throws JsonParseException;

    public abstract void updateDataLocally(Context c);

    public abstract int getNotificationId();

    protected String getLogTag(){
        if(logTag == null) {
            logTag = Constants.LOG_TAG + "/" + messageType;
        }
        return logTag;
    }

}
