package com.thebluealliance.androidclient.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.thebluealliance.androidclient.TbaAndroid;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.NotificationsTable;

import javax.inject.Inject;

/**
 * Android is predictably stupid when it comes to handling notifications. Specifically, clicking
 * a notification with setAutoCancel() set to true won't trigger the delete intent of the
 * notification, even though the notification has in fact been deleted:
 * http://stackoverflow.com/questions/13078230/. This causes problems with notification stacking
 * because the notification is never set to not active when it's clicked. To solve this problem,
 * notifications now send a broadcast to this receiver when they are either clicked or deleted.
 * Depending on the action specified in the broadcast intent, this may launch an activity specified
 * in the intent's extras. However, it will always mark all notifications as not active.
 */
public class NotificationChangedReceiver extends BroadcastReceiver {

    public static final String ACTION_NOTIFICATION_CLICKED = "com.thebluealliance.androidclient.intent.action.NOTIFICATION_CLICKED";
    public static final String ACTION_NOTIFICATION_DELETED = "com.thebluealliance.androidclient.intent.action.NOTIFICATION_DELETED";

    public static final String EXTRA_INTENT = "com.thebluealliance.androidclient.intent.extra.INTENT";
    public static final String EXTRA_NOTIFICATION_ID = "com.thebluealliance.androidclient.intent.extra.NOTIFICATION_ID";

    public static Intent newIntent(Context context) {
        return new Intent(context, NotificationChangedReceiver.class);
    }

    @Inject Database mDb;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((TbaAndroid)context.getApplicationContext()).getDbComponent().inject(this);
        if (intent.getAction().equals(ACTION_NOTIFICATION_CLICKED)) {
            // Check for an intent to launch
            Bundle extras = intent.getExtras();
            if (extras.containsKey(EXTRA_INTENT)) {
                Intent extraIntent = extras.getParcelable(EXTRA_INTENT);
                System.out.println("Intent: " + extraIntent.toString());
                extraIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(extraIntent);
            }
        }

        // Mark all notifications as not active
        TbaLogger.d("Notification Dismiss!");
        NotificationsTable table = Database.getInstance(context).getNotificationsTable();
        table.dismissAll();
    }
}
