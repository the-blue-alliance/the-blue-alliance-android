package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.thebluealliance.androidclient.database.tables.SubscriptionsTable;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.helpers.ModelType;

import java.util.ArrayList;
import java.util.List;

/**
 * File created by phil on 8/13/14.
 */
public class Subscription {
    private String userName;
    private String modelKey;
    private String notificationSettings;
    private List<String> notificationList;
    private int modelEnum;

    public Subscription() {
        notificationList = new ArrayList<>();
    }

    public Subscription(String userName, String modelKey, List<String> notificationSettings, int model_type) {
        this.userName = userName;
        this.modelKey = modelKey;
        this.notificationList = notificationSettings;
        this.notificationSettings = makeNotificationJSON(notificationSettings);
        setModelEnum(model_type);
    }

    public String getKey() {
        return userName + ":" + modelKey;
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

    public String getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(String notificationSettings) {
        this.notificationSettings = notificationSettings;
        // Update the ArrayList
        notificationList.clear();
        for (JsonElement element : JSONHelper.getasJsonArray(notificationSettings)) {
            notificationList.add(element.getAsString());
        }
    }

    public List<String> getNotificationList() {
        if (notificationList == null) {
            notificationList = new ArrayList<>();
            for (JsonElement element : JSONHelper.getasJsonArray(notificationSettings)) {
                notificationList.add(element.getAsString());
            }
        }
        return notificationList;
    }

    public int getModelEnum() {
        return modelEnum;
    }

    public ModelType.MODELS getModelType() {
        return ModelType.MODELS.values()[modelEnum];
    }

    public void setModelEnum(int modelEnum) {
        this.modelEnum = modelEnum;
    }

    private static String makeNotificationJSON(List<String> input) {
        JsonArray out = new JsonArray();
        for (String s : input) {
            out.add(new JsonPrimitive(s));
        }
        return out.toString();
    }

    public ContentValues getParams() {
        ContentValues cv = new ContentValues();
        cv.put(SubscriptionsTable.KEY, getKey());
        cv.put(SubscriptionsTable.USER_NAME, userName);
        cv.put(SubscriptionsTable.MODEL_KEY, modelKey);
        cv.put(SubscriptionsTable.NOTIFICATION_SETTINGS, notificationSettings);
        cv.put(SubscriptionsTable.MODEL_ENUM, modelEnum);
        return cv;
    }
}
