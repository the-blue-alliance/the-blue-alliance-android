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
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.background.UpdateMyTBA;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.gcm.notifications.AllianceSelectionNotification;
import com.thebluealliance.androidclient.gcm.notifications.AwardsPostedNotification;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.gcm.notifications.CompLevelStartingNotification;
import com.thebluealliance.androidclient.gcm.notifications.DistrictPointsUpdatedNotification;
import com.thebluealliance.androidclient.gcm.notifications.GenericNotification;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.gcm.notifications.ScheduleUpdatedNotification;
import com.thebluealliance.androidclient.gcm.notifications.ScoreNotification;
import com.thebluealliance.androidclient.gcm.notifications.UpcomingMatchNotification;
import com.thebluealliance.androidclient.models.StoredNotification;

/**
 * Created by Nathan on 7/24/2014.
 */
public class GCMMessageHandler extends IntentService {
    
    public static final String GROUP_KEY = "tba-android";

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
                    new UpdateMyTBA(c, new RequestParams(true)).execute(UpdateMyTBA.UPDATE_FAVORITES);
                    break;
                case NotificationTypes.UPDATE_SUBSCRIPTIONS:
                    new UpdateMyTBA(c, new RequestParams(true)).execute(UpdateMyTBA.UPDATE_SUBSCRIPTION);
                    break;
                case NotificationTypes.PING:
                case NotificationTypes.BROADCAST:
                    notification = new GenericNotification(c, messageType, messageData);
                    break;
                case NotificationTypes.MATCH_SCORE:
                case "score":
                    notification = new ScoreNotification(messageData);
                    break;
                case NotificationTypes.UPCOMING_MATCH:
                    notification = new UpcomingMatchNotification(messageData);
                    break;
                case NotificationTypes.ALLIANCE_SELECTION:
                    notification = new AllianceSelectionNotification(messageData);
                    break;
                case NotificationTypes.LEVEL_STARTING:
                    notification = new CompLevelStartingNotification(messageData);
                    break;
                case NotificationTypes.SCHEDULE_UPDATED:
                    notification = new ScheduleUpdatedNotification(messageData);
                    break;
                case NotificationTypes.AWARDS:
                    notification = new AwardsPostedNotification(messageData);
                    break;
                case NotificationTypes.DISTRICT_POINTS_UPDATED:
                    notification = new DistrictPointsUpdatedNotification(messageData);
                    break;
            }

            if(notification == null) return;
            try {
                notification.parseMessageData();
            } catch (JsonParseException e){
                Log.e(Constants.LOG_TAG, "Error parsing incoming message json");
                Log.e(Constants.LOG_TAG, e.getMessage());
                e.printStackTrace();
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            boolean enabled = prefs.getBoolean("enable_notifications", true);
            if(enabled){
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

                    boolean headsUpPref = PreferenceManager.getDefaultSharedPreferences(c).getBoolean("notification_headsup", true);
                    if(headsUpPref) {
                        built.priority = priority;
                    }else{
                        built.priority = Notification.PRIORITY_DEFAULT;
                    }

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        String pref = PreferenceManager.getDefaultSharedPreferences(c).getString("notification_visibility","private");
                        switch (pref){
                            case "public":  built.visibility = Notification.VISIBILITY_PUBLIC; break;
                            default:
                            case "private": built.visibility = Notification.VISIBILITY_PRIVATE; break;
                            case "secret":  built.visibility = Notification.VISIBILITY_SECRET; break;
                        }

                        built.category = Notification.CATEGORY_SOCIAL;
                    }
                }
                
                

                if(notification.shouldShow()){
                    notificationManager.notify(notification.getNotificationId(), built); 
                }

                /* Update the data coming from this notification in the local db */
                notification.updateDataLocally(c);
                
                /* Store this notification for future access */
                StoredNotification stored = notification.getStoredNotification();
                if(stored != null){
                    Database.Notifications table = Database.getInstance(c).getNotificationsTable();
                    table.add(stored);
                    table.prune();
                }
            }
        } catch (Exception e) {
            // We probably tried to post a null notification or something like that. Oops...
            e.printStackTrace();
        }
    }
}
