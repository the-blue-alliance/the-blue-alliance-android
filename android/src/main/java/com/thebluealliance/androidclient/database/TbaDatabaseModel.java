package com.thebluealliance.androidclient.database;

import android.content.ContentValues;

import com.google.gson.Gson;

public interface TbaDatabaseModel {

    String getKey();
    Long getLastModified();
    void setLastModified(Long lastModified);
    ContentValues getParams(Gson gson);
}
