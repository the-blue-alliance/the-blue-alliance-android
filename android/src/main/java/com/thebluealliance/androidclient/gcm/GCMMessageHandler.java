package com.thebluealliance.androidclient.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.background.UpdateMyTBA;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.gcm.notifications.CompLevelStartingNotification;
import com.thebluealliance.androidclient.gcm.notifications.GenericNotification;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
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
        Log.d(Constants.LOG_TAG, "GCM Message type: " + messageType);
        Log.d(Constants.LOG_TAG, "Intent extras: " + extras.toString());

        // We got a standard message. Parse it and handle it.
        String type = extras.getString("message_type", "");
        String data = extras.getString("message_data", "");
        handleMessage(getApplicationContext(), type, data);

        Log.i(Constants.LOG_TAG, "Received : (" + type + ")  " + data);

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    public static void handleMessage(Context c, String messageType, String messageData) {
        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            BaseNotification notification = null;
            switch (messageType) {
                case NotificationTypes.UPDATE_FAVORITES:
                    new UpdateMyTBA(c, true).execute(UpdateMyTBA.UPDATE_FAVORITES);
                    break;
                case NotificationTypes.UPDATE_SUBSCRIPTIONS:
                    new UpdateMyTBA(c, true).execute(UpdateMyTBA.UPDATE_SUBSCRIPTION);
                    break;
                case NotificationTypes.PING:
                    notification = new GenericNotification(messageData);
                    break;
                case NotificationTypes.MATCH_SCORE:
                    notification = new ScoreNotification(messageData);
                    break;
                case NotificationTypes.UPCOMING_MATCH:
                    notification = new UpcomingMatchNotification(messageData);
                    break;
                case "score":
                    notification = new ScoreNotification(messageData);
                    break;
                case NotificationTypes.ALLIANCE_SELECTION:
                    break;
                case NotificationTypes.LEVEL_STARTING:
                    notification = new CompLevelStartingNotification(messageData);
                    break;
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            boolean enabled = prefs.getBoolean("enable_notifications", true);
            if(enabled && notification != null){
                Notification built = notification.buildNotification(c);
                if(prefs.getBoolean("notification_vibrate", true)){
                    built.defaults |= Notification.DEFAULT_VIBRATE;
                }
                if(prefs.getBoolean("notification_tone", true)){
                    built.defaults |= Notification.DEFAULT_SOUND;
                }

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    int priority = Notification.PRIORITY_HIGH;
                    switch (messageType){
                        case NotificationTypes.PING: priority = Notification.PRIORITY_LOW;
                    }
                    built.priority = priority;
                }

                notificationManager.notify(notification.getNotificationId(), built);
            }
        } catch (Exception e) {
            // We probably tried to post a null notification or something like that. Oops...
            e.printStackTrace();
        }
    }
}
