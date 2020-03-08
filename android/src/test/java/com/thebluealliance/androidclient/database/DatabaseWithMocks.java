package com.thebluealliance.androidclient.database;

import android.content.Context;

import com.thebluealliance.androidclient.TestTbaAndroid;

public class DatabaseWithMocks extends Database {

    public DatabaseWithMocks(Context context) {
        super(context);
    }

    @Override
    protected void inject(Context context) {
        ((TestTbaAndroid)context.getApplicationContext()).getMockDbComponent().inject(this);
    }
}
