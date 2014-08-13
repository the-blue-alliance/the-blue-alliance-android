package com.thebluealliance.androidclient.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.gcm.notifications.ScoreNotification;
import com.thebluealliance.androidclient.gcm.notifications.UpcomingMatchNotification;

/**
 * Created by Nathan on 7/24/2014.
 */
public class GCMMessageHandler extends IntentService {

    public GCMMessageHandler() {
        super("GCMMessageHandler");
    }

    public GCMMessageHandler(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);
        if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            // We got a standard message. Parse it and handle it.
            String type = extras.getString("message_type", "");
            String data = extras.getString("message_data", "");
            handleMessage(getApplicationContext(), type, data);

            Log.i(Constants.LOG_TAG, "Received : (" + type + ")  " + data);
        }

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    public static void handleMessage(Context c, String messageType, String messageData) {
        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        JsonObject data = JSONManager.getParser().parse(messageData).getAsJsonObject();
        String type = data.get("type").getAsString();
        try {
            switch (type) {
                case "test":
                    Notification notification =
                            new NotificationCompat.Builder(c)
                                    .setSmallIcon(R.drawable.ic_notification)
                                    .setContentTitle(data.get("title").getAsString())
                                    .setContentText(data.get("desc").getAsString()).build();
                    notificationManager.notify(12, notification);
                    break;
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
        } catch (Exception e) {
            // We probably tried to post a null notification or something like that. Oops...
            e.printStackTrace();
        }
    }
}
