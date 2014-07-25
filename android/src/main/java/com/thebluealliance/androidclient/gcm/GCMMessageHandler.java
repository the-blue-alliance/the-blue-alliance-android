package com.thebluealliance.androidclient.gcm;

import android.app.NotificationManager;
import android.content.Context;

import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.gcm.notifications.ScoreNotification;
import com.thebluealliance.androidclient.gcm.notifications.UpcomingMatchNotification;

/**
 * Created by Nathan on 7/24/2014.
 */
public class GCMMessageHandler {

    public static void handleMessage(Context c, String messageType, String messageData) {
        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        switch (messageType) {
            case "upcoming_match":
                BaseNotification n = new UpcomingMatchNotification(messageData);
                // id allows you to update the notification later on.
                notificationManager.notify(n.getNotificationId(), n.buildNotification(c));
                break;
            case "score":
                n = new ScoreNotification(messageData);
                // id allows you to update the notification later on.
                notificationManager.notify(n.getNotificationId(), n.buildNotification(c));
                break;
            case "alliance_selection":
            case "starting_comp_level":
                // TODO implement notifications for these message types
                break;
        }
    }
}
