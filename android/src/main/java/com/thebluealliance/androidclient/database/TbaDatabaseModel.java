package com.thebluealliance.androidclient.database;

import android.content.ContentValues;

public interface TbaDatabaseModel {

    String getKey();
    ContentValues getParams();
}
