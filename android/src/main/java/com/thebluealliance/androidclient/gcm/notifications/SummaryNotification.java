package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.HomeActivity;
import com.thebluealliance.androidclient.gcm.FollowsChecker;
import com.thebluealliance.androidclient.gcm.GCMMessageHandler;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.receivers.NotificationChangedReceiver;

import java.util.List;

public class SummaryNotification extends BaseNotification<Void> {
    /**
     * Limit the summary's list to avoid taking up the whole notification shade and to work around
     * <a href="https://code.google.com/p/android/issues/detail?id=168890">an Android 5.1 bug</a>.
     */
    static final int MAX = 7;

    public static final int NOTIFICATION_ID = 1337;

    private final List<StoredNotification> mActiveNotifications;

    public SummaryNotification(List<StoredNotification> activeNotifications) {
        super(NotificationTypes.SUMMARY, "");
        mActiveNotifications = activeNotifications;
    }

    @Override
    public Notification buildNotification(Context context, FollowsChecker followsChecker) {
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        int size = mActiveNotifications.size();
        int count = 0;

        for (StoredNotification n : mActiveNotifications) {
            if (++count > MAX) {
                break;
            }
            style.addLine(n.getTitle());
        }

        String notificationTitle = context.getString(R.string.notification_summary, size);
        style.setBigContentTitle(notificationTitle);

        style.setSummaryText(
                size > MAX ? context.getString(R.string.notification_summary_more, size - MAX)
                        : context.getString(R.string.app_name));

        Intent instance = getIntent(context);
        PendingIntent intent = makeNotificationIntent(context, instance);

        Intent dismissIntent = NotificationChangedReceiver.newIntent(context);
        dismissIntent.setAction(NotificationChangedReceiver.ACTION_NOTIFICATION_DELETED);
        PendingIntent onDismiss = PendingIntent.getBroadcast(context, 0, dismissIntent, PendingIntent.FLAG_IMMUTABLE);

        return getBaseBuilder(context)
                .setContentTitle(notificationTitle)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setContentIntent(intent)
                .setDeleteIntent(onDismiss)
                .setAutoCancel(true)
                .setGroup(GCMMessageHandler.GROUP_KEY)
                .setGroupSummary(true)
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
        return NOTIFICATION_ID;
    }

    @Nullable
    @Override
    public Void renderToViewModel(Context context, @Nullable Void aVoid) {
        return null;
    }
}
