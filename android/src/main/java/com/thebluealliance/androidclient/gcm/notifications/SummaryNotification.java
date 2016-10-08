package com.thebluealliance.androidclient.gcm.notifications;

import com.google.gson.JsonParseException;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.HomeActivity;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.NotificationsTable;
import com.thebluealliance.androidclient.gcm.FollowsChecker;
import com.thebluealliance.androidclient.gcm.GCMMessageHandler;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.receivers.NotificationChangedReceiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.List;

public class SummaryNotification extends BaseNotification<Void> {
    /**
     * Limit the summary's list to avoid taking up the whole notification shade and to work around
     * <a href="https://code.google.com/p/android/issues/detail?id=168890">an Android 5.1 bug</a>.
     */
    static final int MAX = 7;

    public SummaryNotification() {
        super(NotificationTypes.SUMMARY, "");
    }

    @Override
    public Notification buildNotification(Context context, FollowsChecker followsChecker) {
        NotificationsTable table = Database.getInstance(context).getNotificationsTable();

        List<StoredNotification> active = table.getActive();
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        int size = active.size();
        int count = 0;

        for (StoredNotification n : active) {
            if (++count > MAX) {
                break;
            }
            style.addLine(n.getTitle());
        }

        String notificationTitle = context.getString(R.string.notification_summary, size);
        style.setBigContentTitle(notificationTitle);

        if (!GCMMessageHandler.SUMMARY_NOTIFICATION_IS_A_HEADER) {
            style.setSummaryText(
                    size > MAX ? context.getString(R.string.notification_summary_more, size - MAX)
                               : context.getString(R.string.app_name));
        } // else don't set the summary line since on these Android versions, the header already
          // shows the app name and overflow count. "" would show an extra •,
          // "The Blue Alliance • • now."

        Intent instance = getIntent(context);
        PendingIntent intent = makeNotificationIntent(context, instance);

        Intent dismissIntent = NotificationChangedReceiver.newIntent(context);
        dismissIntent.setAction(NotificationChangedReceiver.ACTION_NOTIFICATION_DELETED);
        PendingIntent onDismiss = PendingIntent.getBroadcast(context, 0, dismissIntent, 0);

        return new NotificationCompat.Builder(context)
                .setContentTitle(notificationTitle)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setContentIntent(intent)
                .setDeleteIntent(onDismiss)
                .setAutoCancel(true)
                .setGroup(GCMMessageHandler.GROUP_KEY)
                .setGroupSummary(GCMMessageHandler.STACK_NOTIFICATIONS)
                .setStyle(style).build();
    }

    @Override
    public void parseMessageData() throws JsonParseException {
        /* Nothing to do */
    }

    @Override
    public Intent getIntent(Context c) {
        return HomeActivity.newInstance(c, R.id.nav_item_notifications);
    }

    @Override
    public void updateDataLocally() {
        /* Nothing to store */
    }

    @Override
    public int getNotificationId() {
        /* All have the same ID so future notifications replace it */
        return 1337;
    }

    /* Checks if we've already posted a notification */
    public static boolean isNotificationActive(Context context) {
        NotificationsTable table = Database.getInstance(context).getNotificationsTable();
        return table.getActive().size() > 1;
        // The newest notification has already been added to the table, so we're checking if there are 2+ active
    }

    @Nullable
    @Override
    public Void renderToViewModel(Context context, @Nullable Void aVoid) {
        return null;
    }
}
