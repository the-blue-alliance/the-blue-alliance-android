package com.thebluealliance.androidclient.gcm;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.thebluealliance.androidclient.R;
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
import com.thebluealliance.androidclient.gcm.notifications.SummaryNotification;
import com.thebluealliance.androidclient.gcm.notifications.TeamMatchVideoNotification;
import com.thebluealliance.androidclient.gcm.notifications.UpcomingMatchNotification;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.mytba.MyTbaRegistrationWorker;
import com.thebluealliance.androidclient.mytba.MyTbaUpdateWorker;
import com.thebluealliance.androidclient.renderers.MatchRenderer;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GCMMessageHandler extends FirebaseMessagingService implements FollowsChecker {

    public static final String GROUP_KEY = "tba-android";

    @Inject MyTbaDatafeed mMyTbaDatafeed;
    @Inject DatabaseWriter mWriter;
    @Inject SharedPreferences mPrefs;
    @Inject EventBus mEventBus;
    @Inject TBAStatusController mStatusController;
    @Inject MatchRenderer mMatchRenderer;
    @Inject Database mDb;
    @Inject AccountController mAccountController;
    @Inject AppConfig mAppConfig;
    @Inject Gson mGson;

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
    public void onNewToken(@NonNull String s) {
        // Kick off the registration service
        WorkManager.getInstance(getApplicationContext())
                .enqueue(new OneTimeWorkRequest.Builder(MyTbaRegistrationWorker.class).build());
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String messageType = remoteMessage.getMessageType();
        Map<String, String> messageData = remoteMessage.getData();
        TbaLogger.d("GCM Message type: " + messageType);
        TbaLogger.d("GCM Message: " + messageData);

        // We got a standard message. Parse it and handle it.
        String type = messageData.containsKey("notification_type") ? messageData.get("notification_type") : "";
        String data = messageData.containsKey("message_data") ? messageData.get("message_data") : "";
        TbaLogger.i("Received Notification : (" + type + ")  " + data);
        try {
            handleMessage(getApplicationContext(), type, data);
            AnalyticsHelper.sendNotificationReceived(getApplicationContext(), type);
            TbaLogger.d("Notification " + type + " processed successfully");
        } catch (Exception e) {
            // We probably tried to post a null notification or something like that. Oops...
            TbaLogger.e("Error parsing notification", e);
            throw e;
        }
    }

    public void handleMessage(Context c, String messageType, String messageData) {
        boolean enabled = mPrefs.getBoolean("enable_notifications", true);
        if (!enabled) {
            TbaLogger.d("Notification disabled locally, bailing early");
            return;
        }

        BaseNotification notification;
        switch (messageType) {
            case NotificationTypes.UPDATE_FAVORITES:
                MyTbaUpdateWorker.run(c, true, false);
                return;
            case NotificationTypes.UPDATE_SUBSCRIPTIONS:
                MyTbaUpdateWorker.run(c, false, true);
                return;
            case NotificationTypes.PING:
            case NotificationTypes.BROADCAST:
                notification = new GenericNotification(messageType, messageData);
                break;
            case NotificationTypes.MATCH_SCORE:
            case "score":
                notification = new ScoreNotification(messageData, mWriter.getMatchWriter().get(), mMatchRenderer, mGson);
                break;
            case NotificationTypes.UPCOMING_MATCH:
                notification = new UpcomingMatchNotification(messageData, mGson);
                break;
            case NotificationTypes.ALLIANCE_SELECTION:
                notification = new AllianceSelectionNotification(messageData, mWriter.getEventWriter().get(), mGson);
                break;
            case NotificationTypes.LEVEL_STARTING:
                notification = new CompLevelStartingNotification(messageData);
                break;
            case NotificationTypes.SCHEDULE_UPDATED:
                notification = new ScheduleUpdatedNotification(messageData);
                break;
            case NotificationTypes.AWARDS:
                notification = new AwardsPostedNotification(messageData, mWriter.getAwardListWriter().get(), mGson);
                break;
            case NotificationTypes.DISTRICT_POINTS_UPDATED:
                notification = new DistrictPointsUpdatedNotification(messageData);
                break;
            case NotificationTypes.TEAM_MATCH_VIDEO:
                notification = new TeamMatchVideoNotification(messageData, mWriter.getMatchWriter().get(), mGson);
                break;
            case NotificationTypes.EVENT_MATCH_VIDEO:
                notification = new EventMatchVideoNotification(messageData, mGson);
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
        NotificationsTable storedNotificationTable = mDb.getNotificationsTable();
        StoredNotification stored = notification.getStoredNotification();
        if (stored != null) {
            storedNotificationTable.add(stored);
            storedNotificationTable.prune();
        }

        // Tell interested parties that a new notification has arrived
        mEventBus.post(new NotificationsUpdatedEvent(notification));

        if (!notification.shouldShow()) {
            TbaLogger.d("Not displaying notification type " + messageType);
            return;
        }

        List<StoredNotification> activeNotifications = storedNotificationTable.getActive();
        notify(c, notification, built, activeNotifications);
    }

    protected void notify(Context c, BaseNotification notification, Notification built, List<StoredNotification> activeNotifications) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(c);
        int id = notification.getNotificationId();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(c, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            TbaLogger.w("Notification permission not granted! Skipping posting notifications...");
            return;
        }

        setNotificationParams(built, c, notification.getNotificationType(), mPrefs);
        TbaLogger.i("Notifying: " + id);
        notificationManager.notify(id, built);

        if (activeNotifications.size() > 1) {
            TbaLogger.i("Posting summary for " + activeNotifications.size() + " notifications");
            SummaryNotification summary = new SummaryNotification(activeNotifications);
            notificationManager.notify(summary.getNotificationId(), summary.buildNotification(c, this));
        }
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

        built.visibility = Notification.VISIBILITY_PUBLIC;
        built.category = Notification.CATEGORY_SOCIAL;
    }
}
