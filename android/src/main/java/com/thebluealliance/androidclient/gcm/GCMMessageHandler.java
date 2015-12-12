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
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.background.UpdateMyTBA;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.database.tables.NotificationsTable;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.di.components.DaggerNotificationComponent;
import com.thebluealliance.androidclient.di.components.NotificationComponent;
import com.thebluealliance.androidclient.eventbus.NotificationsUpdatedEvent;
import com.thebluealliance.androidclient.gcm.notifications.AllianceSelectionNotification;
import com.thebluealliance.androidclient.gcm.notifications.AwardsPostedNotification;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.gcm.notifications.CompLevelStartingNotification;
import com.thebluealliance.androidclient.gcm.notifications.DistrictPointsUpdatedNotification;
import com.thebluealliance.androidclient.gcm.notifications.GenericNotification;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.gcm.notifications.ScheduleUpdatedNotification;
import com.thebluealliance.androidclient.gcm.notifications.ScoreNotification;
import com.thebluealliance.androidclient.gcm.notifications.SummaryNotification;
import com.thebluealliance.androidclient.gcm.notifications.UpcomingMatchNotification;
import com.thebluealliance.androidclient.models.StoredNotification;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class GCMMessageHandler extends IntentService {

    public static final String GROUP_KEY = "tba-android";

    @Inject
    MyTbaDatafeed mMyTbaDatafeed;
    @Inject
    DatabaseWriter mWriter;
    @Inject
    SharedPreferences mPrefs;
    @Inject
    EventBus mEventBus;

    private NotificationComponent mComponenet;

    public GCMMessageHandler() {
        this("GCMMessageHandler");
    }

    public GCMMessageHandler(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getComponenet();
        mComponenet.inject(this);
    }

    private void getComponenet() {
        if (mComponenet == null) {
            TBAAndroid application = ((TBAAndroid) getApplication());
            mComponenet = DaggerNotificationComponent.builder()
                    .applicationComponent(application.getComponent())
                    .datafeedModule(application.getDatafeedModule())
                    .databaseWriterModule(application.getDatabaseWriterModule())
                    .build();
        }
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

    public void handleMessage(Context c, String messageType, String messageData) {
        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            BaseNotification notification = null;
            switch (messageType) {
                case NotificationTypes.UPDATE_FAVORITES:
                    new UpdateMyTBA(mMyTbaDatafeed).execute(UpdateMyTBA.UPDATE_FAVORITES);
                    break;
                case NotificationTypes.UPDATE_SUBSCRIPTIONS:
                    new UpdateMyTBA(mMyTbaDatafeed).execute(UpdateMyTBA.UPDATE_SUBSCRIPTION);
                    break;
                case NotificationTypes.PING:
                case NotificationTypes.BROADCAST:
                    notification = new GenericNotification(c, messageType, messageData);
                    break;
                case NotificationTypes.MATCH_SCORE:
                case "score":
                    notification = new ScoreNotification(messageData, mWriter.getMatchWriter().get());
                    break;
                case NotificationTypes.UPCOMING_MATCH:
                    notification = new UpcomingMatchNotification(messageData);
                    break;
                case NotificationTypes.ALLIANCE_SELECTION:
                    notification = new AllianceSelectionNotification(messageData, mWriter.getEventWriter().get());
                    break;
                case NotificationTypes.LEVEL_STARTING:
                    notification = new CompLevelStartingNotification(messageData);
                    break;
                case NotificationTypes.SCHEDULE_UPDATED:
                    notification = new ScheduleUpdatedNotification(messageData);
                    break;
                case NotificationTypes.AWARDS:
                    notification = new AwardsPostedNotification(messageData, mWriter.getAwardListWriter().get());
                    break;
                case NotificationTypes.DISTRICT_POINTS_UPDATED:
                    notification = new DistrictPointsUpdatedNotification(messageData);
                    break;
            }

            if (notification == null) return;
            try {
                notification.parseMessageData();
            } catch (JsonParseException e) {
                Log.e(Constants.LOG_TAG, "Error parsing incoming message json");
                e.printStackTrace();
                return;
            }

            boolean enabled = mPrefs.getBoolean("enable_notifications", true);
            if (enabled) {
                Notification built;

                built = notification.buildNotification(c);
                if (built == null) return;

                /* Update the data coming from this notification in the local db */
                notification.updateDataLocally();

                /* Store this notification for future access */
                StoredNotification stored = notification.getStoredNotification();
                if (stored != null) {
                    NotificationsTable table = Database.getInstance(c).getNotificationsTable();
                    table.add(stored);
                    table.prune();
                }

                if (notification.shouldShow()) {
                    if (SummaryNotification.isNotificationActive(c)) {
                        notification = new SummaryNotification();
                        built = notification.buildNotification(c);
                    }

                    setNotificationParams(built, c, messageType, mPrefs);
                    int id = notification.getNotificationId();
                    notificationManager.notify(id, built);
                }

                mEventBus.post(new NotificationsUpdatedEvent(notification));
            }
        } catch (Exception e) {
            // We probably tried to post a null notification or something like that. Oops...
            e.printStackTrace();
        }
    }

    private static void setNotificationParams(Notification built, Context c, String messageType, SharedPreferences prefs) {
        /* Set notification parameters */
        if (prefs.getBoolean("notification_vibrate", true)) {
            built.defaults |= Notification.DEFAULT_VIBRATE;
        }
        if (prefs.getBoolean("notification_tone", true)) {
            built.defaults |= Notification.DEFAULT_SOUND;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            int priority = Notification.PRIORITY_HIGH;
            switch (messageType) {
                case NotificationTypes.PING:
                    priority = Notification.PRIORITY_LOW;
            }

            boolean headsUpPref = PreferenceManager.getDefaultSharedPreferences(c).getBoolean("notification_headsup", true);
            if (headsUpPref) {
                built.priority = priority;
            } else {
                built.priority = Notification.PRIORITY_DEFAULT;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                built.visibility = Notification.VISIBILITY_PUBLIC;
                built.category = Notification.CATEGORY_SOCIAL;
            }
        }
    }
}
