package com.thebluealliance.androidclient.gcm;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.JsonParseException;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.database.tables.FavoritesTable;
import com.thebluealliance.androidclient.database.tables.NotificationsTable;
import com.thebluealliance.androidclient.database.tables.SubscriptionsTable;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.datafeed.status.TBAStatusController;
import com.thebluealliance.androidclient.di.components.DaggerNotificationComponent;
import com.thebluealliance.androidclient.di.components.NotificationComponent;
import com.thebluealliance.androidclient.eventbus.NotificationsUpdatedEvent;
import com.thebluealliance.androidclient.gcm.notifications.AllianceSelectionNotification;
import com.thebluealliance.androidclient.gcm.notifications.AwardsPostedNotification;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.gcm.notifications.CompLevelStartingNotification;
import com.thebluealliance.androidclient.gcm.notifications.DistrictPointsUpdatedNotification;
import com.thebluealliance.androidclient.gcm.notifications.EventDownNotification;
import com.thebluealliance.androidclient.gcm.notifications.GenericNotification;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.gcm.notifications.ScheduleUpdatedNotification;
import com.thebluealliance.androidclient.gcm.notifications.ScoreNotification;
import com.thebluealliance.androidclient.gcm.notifications.SummaryNotification;
import com.thebluealliance.androidclient.gcm.notifications.UpcomingMatchNotification;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.mytba.MyTbaUpdateService;
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.renderers.RendererModule;

import org.greenrobot.eventbus.EventBus;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import javax.inject.Inject;

public class GCMMessageHandler extends IntentService implements FollowsChecker {

    /**
     * Stack (bundle) notifications together into a Group for better UX on Nougat+ and Android Wear
     * but not on KitKat because SupportLib 24.2.1 NotificationManagerCompat drops grouped
     * notifications (http://stackoverflow.com/a/34953411/1682419) nor on Lollipop API 21 because
     * the OS messes up groups (it shows a summary and the first two source notifications as
     * separate items instead of one group.)
     */
    public static final boolean STACK_NOTIFICATIONS =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
    /** True if phones/tablets will bundle up the stack using the summary as a header. */
    public static final boolean SUMMARY_NOTIFICATION_IS_A_HEADER =
            STACK_NOTIFICATIONS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    /** The setGroup() key to group notifications into a stack/bundle as feasible. */
    public static final String GROUP_KEY = STACK_NOTIFICATIONS ? "tba-android" : null;
    /** If grouping won't work, use this ID to make each notification replace its predecessor. */
    public static final int SINGULAR_NOTIFICATION_ID = 363;

    @Inject MyTbaDatafeed mMyTbaDatafeed;
    @Inject DatabaseWriter mWriter;
    @Inject SharedPreferences mPrefs;
    @Inject EventBus mEventBus;
    @Inject TBAStatusController mStatusController;
    @Inject MatchRenderer mMatchRenderer;
    @Inject Database mDb;
    @Inject AccountController mAccountController;

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
                    .rendererModule(new RendererModule())
                    .build();
        }
    }

    @Override
    public boolean followsTeam(Context context, String teamNumber, String matchKey,
                               String notificationType) {
        String currentUser = mAccountController.getSelectedAccount();
        String teamKey = TeamHelper.baseTeamKey("frc" + teamNumber); // "frc111"
        String teamInterestKey = MyTBAHelper.createKey(currentUser, teamKey); // "r@gmail.com:frc111"
        String teamAtEventKey = EventTeamHelper.generateKey(
                MatchHelper.getEventKeyFromMatchKey(matchKey), teamKey); // "2016calb_frc111"
        String teamAtEventInterestKey = MyTBAHelper.createKey(currentUser, teamAtEventKey);
        FavoritesTable favTable = mDb.getFavoritesTable();
        SubscriptionsTable subTable = mDb.getSubscriptionsTable();

        return favTable.exists(teamInterestKey)
                || favTable.exists(teamAtEventInterestKey)
                || subTable.hasNotificationType(teamInterestKey, notificationType)
                || subTable.hasNotificationType(teamAtEventInterestKey, notificationType);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);
        TbaLogger.d("GCM Message type: " + messageType);
        TbaLogger.d("Intent extras: " + extras.toString());

        // We got a standard message. Parse it and handle it.
        String type = extras.getString("message_type", "");
        String data = extras.getString("message_data", "");
        handleMessage(getApplicationContext(), type, data);

        TbaLogger.i("Received : (" + type + ")  " + data);

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    public void handleMessage(Context c, String messageType, String messageData) {
        try {
            BaseNotification notification = null;
            switch (messageType) {
                case NotificationTypes.UPDATE_FAVORITES:
                    Intent favIntent = MyTbaUpdateService.newInstance(c, true, false);
                    c.startService(favIntent);
                    break;
                case NotificationTypes.UPDATE_SUBSCRIPTIONS:
                    Intent subIntent = MyTbaUpdateService.newInstance(c, false, true);
                    c.startService(subIntent);
                    break;
                case NotificationTypes.PING:
                case NotificationTypes.BROADCAST:
                    notification = new GenericNotification(messageType, messageData);
                    break;
                case NotificationTypes.MATCH_SCORE:
                case "score":
                    notification = new ScoreNotification(messageData, mWriter.getMatchWriter().get(), mMatchRenderer);
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
                case NotificationTypes.EVENT_DOWN:
                    notification = new EventDownNotification(messageData);
                    /* Don't break, we also want to schedule a status update here */
                case NotificationTypes.SYNC_STATUS:
                    TbaLogger.i("Updating TBA API Status via push notification");
                    mStatusController.scheduleStatusUpdate(c);
                    break;
            }

            if (notification == null) return;

            try {
                notification.parseMessageData();
            } catch (JsonParseException e) {
                TbaLogger.e("Error parsing incoming message json");
                e.printStackTrace();
                return;
            }

            boolean enabled = mPrefs.getBoolean("enable_notifications", true);
            if (enabled) {
                Notification built;

                built = notification.buildNotification(c, this);
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

                // Tell interested parties that a new notification has arrived
                mEventBus.post(new NotificationsUpdatedEvent(notification));

                if (notification.shouldShow()) {
                    if (SummaryNotification.isNotificationActive(c)) {
                        // Multiple notifications: Stack them into a Group by posting the new
                        // notification THEN (re)posting a summary. If we can't stack them, post
                        // the new one XOR a summary, all with the same ID to replace any
                        // predecessor notification.
                        if (STACK_NOTIFICATIONS) {
                            notify(c, notification, built);
                        }

                        notification = new SummaryNotification();
                        built = notification.buildNotification(c, this);
                    }

                    notify(c, notification, built);
                }
            }
        } catch (Exception e) {
            // We probably tried to post a null notification or something like that. Oops...
            e.printStackTrace();
        }
    }

    private void notify(Context c, BaseNotification notification, Notification built) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(c);
        int id = STACK_NOTIFICATIONS ? notification.getNotificationId() : SINGULAR_NOTIFICATION_ID;

        setNotificationParams(built, c, notification.getNotificationType(), mPrefs);
        notificationManager.notify(id, built);
    }

    private static void setNotificationParams(Notification built, Context c, String messageType, SharedPreferences prefs) {
        /* Set notification parameters */
        if (prefs.getBoolean("notification_vibrate", true)) {
            built.defaults |= Notification.DEFAULT_VIBRATE;
        }
        if (prefs.getBoolean("notification_tone", true)) {
            built.defaults |= Notification.DEFAULT_SOUND;
        }
        if (prefs.getBoolean("notification_led_enabled", true)) {
            built.ledARGB = prefs.getInt("notification_led_color",
                    ContextCompat.getColor(c, R.color.primary));
            built.ledOnMS = 1000;
            built.ledOffMS = 1000;
            built.flags |= Notification.FLAG_SHOW_LIGHTS;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            int priority = Notification.PRIORITY_HIGH;
            switch (messageType) {
                case NotificationTypes.PING:
                    priority = Notification.PRIORITY_LOW;
                    break;
                case NotificationTypes.SUMMARY:
                    // If Android will really display a component notification then a group summary,
                    // don't let the summary heads-up atop the component.
                    if (SUMMARY_NOTIFICATION_IS_A_HEADER) {
                        priority = Notification.PRIORITY_DEFAULT;
                    }
                    break;
            }

            boolean headsUpPref = prefs.getBoolean("notification_headsup", true);
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
