package com.thebluealliance.androidclient.database;

import android.content.ContentValues;

import com.google.gson.Gson;

public interface TbaDatabaseModel {

    String getKey();
    ContentValues getParams(Gson gson);
}
