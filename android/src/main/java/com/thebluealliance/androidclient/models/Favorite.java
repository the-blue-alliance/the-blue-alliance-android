package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.thebluealliance.androidclient.database.tables.FavoritesTable;
import com.thebluealliance.androidclient.helpers.ModelType;

/**
 * File created by phil on 8/13/14.
 */
public class Favorite {

    private String userName;
    private String modelKey;
    private int modelEnum;

    public Favorite() {

    }

    public Favorite(String userName, String modelKey, int model_type) {
        this.userName = userName;
        this.modelKey = modelKey;
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

    public int getModelEnum() {
        return modelEnum;
    }

    public ModelType getModelType() {
        return ModelType.values()[modelEnum];
    }

    public void setModelEnum(int modelEnum) {
        this.modelEnum = modelEnum;
    }

    public ContentValues getParams() {
        ContentValues cv = new ContentValues();
        cv.put(FavoritesTable.KEY, getKey());
        cv.put(FavoritesTable.USER_NAME, userName);
        cv.put(FavoritesTable.MODEL_KEY, modelKey);
        cv.put(FavoritesTable.MODEL_ENUM, modelEnum);
        return cv;
    }
}
