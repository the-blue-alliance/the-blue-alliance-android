package com.thebluealliance.androidclient.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.NotificationsTable;

/**
 * Created by phil on 2/5/15.
 */
public class NotificationDismissedListener extends BroadcastReceiver {

    public static Intent newIntent(Context context) {
        return new Intent(context, NotificationDismissedListener.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Constants.LOG_TAG, "Notification Dismiss!");
        NotificationsTable table = Database.getInstance(context).getNotificationsTable();
        table.dismissAll();
    }
}
