package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.gcm.FollowsChecker;
import com.thebluealliance.androidclient.gcm.GCMMessageHandler;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.receivers.NotificationChangedReceiver;
import com.thebluealliance.androidclient.viewmodels.ViewModelRenderer;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

public abstract class BaseNotification<VIEWMODEL> extends ListElement implements ViewModelRenderer<VIEWMODEL, Void> {

    public static String NOTIFICATION_CHANNEL = "mytba_notification";

    String messageData;
    String messageType;
    protected boolean display;
    StoredNotification stored;
    protected Date notificationTime;

    /**
     * Constructor to create from incoming gcm/firebase (json blob of data)
     */
    public BaseNotification(String messageType, String messageData) {
        this.messageType = messageType;
        this.messageData = messageData;
        this.display = true;
        this.stored = null;

        // Set default time to the time this object was created
        setDate(Calendar.getInstance().getTime());
    }

    public void setDate(Date date) {
        notificationTime = date;
    }

    public boolean shouldShow() {
        return display;
    }

    public boolean shouldShowInRecentNotificationsList() {
        return true;
    }

    public abstract Notification buildNotification(Context context, FollowsChecker followsChecker);

    public String getNotificationType() {
        return messageType;
    }

    public abstract void parseMessageData() throws JsonParseException;

    public abstract void updateDataLocally();

    public abstract int getNotificationId();

    /**
     * Get the intent to open whatever this notification's click action is Precondition:
     * parseMessageData has been called
     *
     * @param c Context to use while creating the intent
     * @return This notification's intent (may be null if none)
     */
    public abstract Intent getIntent(Context c);

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        convertView = inflater.inflate(R.layout.list_item_carded_summary, null);
        TextView label = (TextView) convertView.findViewById(R.id.label);
        TextView value = (TextView) convertView.findViewById(R.id.value);
        label.setText(messageType);
        value.setText(messageData);
        return convertView;
    }

    /**
     * Most notifications will build their stored notification in {@code buildNotification}, so
     * this method should be called after that.
     */
    @Nullable
    public StoredNotification getStoredNotification() {
        return stored;
    }

    /**
     * Wraps an intent in an intent to NotificationChangedReceiver
     */
    protected PendingIntent makeNotificationIntent(Context context, Intent activityIntent) {
        int notificationId = getNotificationId();
        Intent clickIntent = NotificationChangedReceiver.newIntent(context);
        clickIntent.setAction(NotificationChangedReceiver.ACTION_NOTIFICATION_CLICKED);
        clickIntent.putExtra(NotificationChangedReceiver.EXTRA_INTENT, activityIntent);
        clickIntent.putExtra(NotificationChangedReceiver.EXTRA_NOTIFICATION_ID, notificationId);
        return PendingIntent.getBroadcast(context, notificationId, clickIntent, PendingIntent.FLAG_IMMUTABLE);
    }

    /**
     * Creates a builder with the important defaults set.
     */
    public NotificationCompat.Builder getBaseBuilder(Context context) {
        Intent dismissIntent = NotificationChangedReceiver.newIntent(context);
        dismissIntent.setAction(NotificationChangedReceiver.ACTION_NOTIFICATION_DELETED);
        dismissIntent.putExtra(NotificationChangedReceiver.EXTRA_NOTIFICATION_ID, getNotificationId());
        PendingIntent onDismiss = PendingIntent.getBroadcast(context, 0, dismissIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender();
        wearableExtender.setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.tba_blue_background));

        return new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setGroup(GCMMessageHandler.GROUP_KEY)
                .setDeleteIntent(onDismiss)
                .setAutoCancel(true)
                .extend(wearableExtender);
    }

    /**
     * Creates a builder with the important defaults and an Activity content intent.
     * <p>
     * <p/>SIDE EFFECTS: Adds a Category to activityIntent so the launched Activity can tell it was
     * triggered by a notification. (Note: Just adding an Extra won't work because Android will
     * retrieve the existing intent, ignoring the new Extra.)
     */
    public NotificationCompat.Builder getBaseBuilder(Context context, Intent activityIntent) {
        NotificationCompat.Builder builder = getBaseBuilder(context);
        PendingIntent onTap = makeNotificationIntent(context, activityIntent);

        builder.setContentIntent(onTap);
        return builder;
    }

    public String getNotificationTimeString(Context c) {
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(c);
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(c);
        if (notificationTime == null) return "";
        return dateFormat.format(notificationTime) + " " + timeFormat.format(notificationTime);
    }

    public String getNotificationCardHeader(Context context, String eventName, String eventKey) {
        String shortName = EventHelper.shortName(eventName);
        String shortCode = EventHelper.getShortCodeForEventKey(eventKey).toUpperCase();
        return context.getString(R.string.gameday_ticker_event_title_format, shortName, shortCode);
    }
}
