package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.NotificationDashboardActivity;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.gcm.GCMMessageHandler;
import com.thebluealliance.androidclient.listeners.NotificationDismissedListener;
import com.thebluealliance.androidclient.models.StoredNotification;

import java.util.ArrayList;

/**
 * Created by phil on 2/5/15.
 */
public class SummaryNotification extends BaseNotification {

    public SummaryNotification(){
        super(NotificationTypes.SUMMARY, "");
    }
    
    @Override
    public Notification buildNotification(Context context) {
        Database.Notifications table = Database.getInstance(context).getNotificationsTable();
        
        ArrayList<StoredNotification> active = table.getActive();
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        for(StoredNotification n: active){
            style.addLine(n.getTitle());
        }
        style.setBigContentTitle("All Notifications"); //TODO to resource
        style.setSummaryText(context.getString(R.string.app_name));
        
        PendingIntent intent = PendingIntent.getActivity(context, 0, NotificationDashboardActivity.newInstance(context), 0);
        PendingIntent onDismiss = PendingIntent.getBroadcast(context, 0, new Intent(context, NotificationDismissedListener.class), 0);
        
        Notification summary = new NotificationCompat.Builder(context)
                .setContentTitle("All Notifications") //TODO move to resource
                .setContentText(String.format("%d new notifications", active.size()))
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(getLargeIconFormattedForPlatform(context, R.drawable.ic_info_outline_white_24dp))
                .setContentIntent(intent)
                .setDeleteIntent(onDismiss)
                .setAutoCancel(true)
                .setGroup(GCMMessageHandler.GROUP_KEY)
                .setGroupSummary(true)
                .setStyle(style).build();
        
        return summary;
    }

    @Override
    public void parseMessageData() throws JsonParseException {
        /* Nothing to do */
    }

    @Override
    public void updateDataLocally(Context c) {
        /* Nothing to store */
    }

    @Override
    public int getNotificationId() {
        /* All have the same ID so future notifications replace it */
        return 1337;
    }
    
    /* Checks if we've already posted a notification */
    public static boolean isNotificationActive(Context context){
        Database.Notifications table = Database.getInstance(context).getNotificationsTable();
        return !table.getActive().isEmpty();
    }
}
