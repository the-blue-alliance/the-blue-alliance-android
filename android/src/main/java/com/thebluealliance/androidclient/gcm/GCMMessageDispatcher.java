package com.thebluealliance.androidclient.gcm;

import android.app.NotificationManager;
import android.content.Context;

import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.gcm.notifications.UpcomingMatchNotification;

/**
 * Created by Nathan on 7/24/2014.
 */
public class GCMMessageDispatcher {

    public static void dispatchMessage(Context c, String messageType, String messageData) {
        switch (messageType) {
            case "upcoming_match":
                BaseNotification n = new UpcomingMatchNotification(messageData);
                NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                mNotificationManager.notify(n.getNotificationId(), n.buildNotification(c));
                break;
            case "score_update":
            case "alliance_selection":
            case "starting_comp_level":
                // TODO implement notifications for these message types
                break;
        }
    }
}
