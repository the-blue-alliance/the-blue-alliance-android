package com.thebluealliance.androidclient.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.Database;

/**
 * Created by phil on 2/5/15.
 */
public class NotificationDismissedListener extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Constants.LOG_TAG, "Notification Dismiss!");
        Database.Notifications table = Database.getInstance(context).getNotificationsTable();
        table.dismissAll();
    }
}
