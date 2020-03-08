package com.thebluealliance.androidclient.gcm;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaAndroid;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.database.tables.FavoritesTable;
import com.thebluealliance.androidclient.database.tables.NotificationsTable;
import com.thebluealliance.androidclient.database.tables.SubscriptionsTable;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.datafeed.status.TBAStatusController;
import com.thebluealliance.androidclient.di.components.DaggerNotificationComponent;
import com.thebluealliance.androidclient.eventbus.NotificationsUpdatedEvent;
import com.thebluealliance.androidclient.gcm.notifications.AllianceSelectionNotification;
import com.thebluealliance.androidclient.gcm.notifications.AwardsPostedNotification;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.gcm.notifications.CompLevelStartingNotification;
import com.thebluealliance.androidclient.gcm.notifications.DistrictPointsUpdatedNotification;
import com.thebluealliance.androidclient.gcm.notifications.EventDownNotification;
import com.thebluealliance.androidclient.gcm.notifications.EventMatchVideoNotification;
import com.thebluealliance.androidclient.gcm.notifications.GenericNotification;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.gcm.notifications.ScheduleUpdatedNotification;
import com.thebluealliance.androidclient.gcm.notifications.ScoreNotification;
import com.thebluealliance.androidclient.gcm.notifications.TeamMatchVideoNotification;
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

import javax.inject.Inject;

import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class GCMMessageHandler extends JobIntentService implements FollowsChecker {

    public static final String GROUP_KEY = "tba-android";
    public static final int JOB_ID = 254;

    @Inject
    GoogleCloudMessaging mCloudMessaging;
    @Inject
    MyTbaDatafeed mMyTbaDatafeed;
    @Inject
    DatabaseWriter mWriter;
    @Inject
    SharedPreferences mPrefs;
    @Inject
    EventBus mEventBus;
    @Inject
    TBAStatusController mStatusController;
    @Inject
    MatchRenderer mMatchRenderer;
    @Inject
    Database mDb;
    @Inject
    AccountController mAccountController;
    @Inject
    AppConfig mAppConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        inject();
    }

    protected void inject() {
        TbaAndroid application = ((TbaAndroid) getApplication());
        DaggerNotificationComponent.builder()
                .applicationComponent(application.getComponent())
                .datafeedModule(application.getDatafeedModule())
                .rendererModule(new RendererModule())
                .authModule(application.getAuthModule())
                .gcmModule(application.getGcmModule())
                .build()
                .inject(this);
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

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, GCMMessageHandler.class, JOB_ID, work);
    }

    @Override
    public void onHandleWork(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            TbaLogger.w("Intent with no extras!");
            return;
        }

        String messageType = mCloudMessaging.getMessageType(intent);
        TbaLogger.d("GCM Message type: " + messageType);
        TbaLogger.d("Intent extras: " + extras.toString());

        // We got a standard message. Parse it and handle it.
        String type = extras.getString("notification_type", "");
        String data = extras.getString("message_data", "");
        TbaLogger.i("Received Notification : (" + type + ")  " + data);
        try {
            handleMessage(getApplicationContext(), type, data);
            TbaLogger.d("Notification " + type + " processed successfully");
        } catch (Exception e) {
            // We probably tried to post a null notification or something like that. Oops...
            TbaLogger.e("Error parsing notification", e);
        }
    }

    public void handleMessage(Context c, String messageType, String messageData) {
        boolean enabled = mPrefs.getBoolean("enable_notifications", true);
        if (!enabled) {
            TbaLogger.d("Notification disabled locally, bailing early");
            return;
        }

        BaseNotification notification = null;
        switch (messageType) {
            case NotificationTypes.UPDATE_FAVORITES:
                Intent favIntent = MyTbaUpdateService.newInstance(c, true, false);
                c.startService(favIntent);
                return;
            case NotificationTypes.UPDATE_SUBSCRIPTIONS:
                Intent subIntent = MyTbaUpdateService.newInstance(c, false, true);
                c.startService(subIntent);
                return;
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
            case NotificationTypes.TEAM_MATCH_VIDEO:
                notification = new TeamMatchVideoNotification(messageData, mWriter.getMatchWriter().get());
                break;
            case NotificationTypes.EVENT_MATCH_VIDEO:
                notification = new EventMatchVideoNotification(messageData);
                break;
            case NotificationTypes.EVENT_DOWN:
                notification = new EventDownNotification(messageData);
                mStatusController.scheduleStatusUpdate(c);
                break;
            case NotificationTypes.SYNC_STATUS:
                TbaLogger.i("Updating TBA API Status via push notification");
                mStatusController.scheduleStatusUpdate(c);
                return;
            default:
                TbaLogger.w("Unknown notification for type " + messageType);
                return;
        }

        notification.parseMessageData();

        Notification built;
        built = notification.buildNotification(c, this);

        /* Update the data coming from this notification in the local db */
        notification.updateDataLocally();

        /* Store this notification for future access */
        StoredNotification stored = notification.getStoredNotification();
        if (stored != null) {
            NotificationsTable table = mDb.getNotificationsTable();
            table.add(stored);
            table.prune();
        }

        // Tell interested parties that a new notification has arrived
        mEventBus.post(new NotificationsUpdatedEvent(notification));

        if (!notification.shouldShow()) {
            TbaLogger.d("Not displaying notification type " + messageType);
            return;
        }

        notify(c, notification, built);
    }

    private void notify(Context c, BaseNotification notification, Notification built) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(c);
        int id = notification.getNotificationId();

        setNotificationParams(built, c, notification.getNotificationType(), mPrefs);
        TbaLogger.i(("Notifying: " + id));
        notificationManager.notify(id, built);
    }

    private static Uri getSoundUri(Context c, int soundId) {
        return Uri.parse("android.resource://" + c.getPackageName() + "/" + soundId);
    }

    private static void setNotificationParams(Notification built, Context c, String messageType, SharedPreferences prefs) {
        /* Set notification parameters */
        if (prefs.getBoolean("notification_vibrate", true)) {
            // Delay vibration to match the system audio delay. Pulse with the beat.
            built.vibrate = new long[]{200, 70, 90, 70, 90, 80};
        }
        if (prefs.getBoolean("notification_tone", true)) {
            built.sound = getSoundUri(c, R.raw.something_you_dont_mess_with);
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
