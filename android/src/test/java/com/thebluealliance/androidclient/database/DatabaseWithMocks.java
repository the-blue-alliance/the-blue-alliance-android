package com.thebluealliance.androidclient.database;

import android.content.Context;

import com.google.gson.Gson;

public class DatabaseWithMocks extends Database {

    public DatabaseWithMocks(Context context, Gson gson) {
        super(context, gson);
    }
}
