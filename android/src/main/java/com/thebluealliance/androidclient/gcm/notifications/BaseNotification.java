package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.gcm.GCMMessageHandler;
import com.thebluealliance.androidclient.listeners.NotificationDismissedListener;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.models.StoredNotification;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Nathan on 7/24/2014.
 */
public abstract class BaseNotification extends ListElement {

    String messageData;
    String messageType;
    Gson gson;
    private String logTag;
    protected boolean display;
    StoredNotification stored;
    protected Date notificationTime;

    public BaseNotification(String messageType, String messageData) {
        this.messageType = messageType;
        this.messageData = messageData;
        this.gson = JSONHelper.getGson();
        this.logTag = null;
        this.display = true;
        this.stored = null;
    }

    public void setDate(Date date) {
        notificationTime = date;
    }

    public boolean shouldShow() {
        return display;
    }

    public abstract Notification buildNotification(Context context);

    public String getNotificationType() {
        return messageType;
    }

    public abstract void parseMessageData() throws JsonParseException;

    public abstract void updateDataLocally(Context c);

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

    public StoredNotification getStoredNotification() {
        return stored;
    }

    protected String getLogTag() {
        if (logTag == null) {
            logTag = Constants.LOG_TAG + "/" + messageType;
        }
        return logTag;
    }

    /**
     * Adds a Category to activityIntent to mark it as "triggered by tapping a system notification",
     * then builds a PendingIntent with it.
     */
    protected PendingIntent makeNotificationIntent(Context context, Intent activityIntent) {
        activityIntent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        return PendingIntent.getActivity(context, getNotificationId(), activityIntent, 0);
    }

    /**
     * Creates a builder with the important defaults set.
     */
    public NotificationCompat.Builder getBaseBuilder(Context context) {
        PendingIntent onDismiss = PendingIntent.getBroadcast(context, 0, NotificationDismissedListener.newIntent(context), 0);

        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setGroup(GCMMessageHandler.GROUP_KEY)
                .setColor(context.getResources().getColor(R.color.accent_dark))
                .setDeleteIntent(onDismiss)
                .setAutoCancel(true)
                .extend(new NotificationCompat.WearableExtender().setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.tba_blue_background)));
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

    protected static Bitmap getLargeIconFormattedForPlatform(Context context, @DrawableRes int drawable) {
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), drawable);
        // Show just the white image on 4.x
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return largeIcon;
        }
        // Add a colored circle on 5.x+
        int padding = Utilities.getPixelsFromDp(context, 8);
        int circleDiameter = Math.max(largeIcon.getWidth(), largeIcon.getHeight()) + (padding * 2);
        Bitmap finalBitmap = Bitmap.createBitmap(circleDiameter, circleDiameter, largeIcon.getConfig());
        Canvas c = new Canvas(finalBitmap);
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(context.getResources().getColor(R.color.primary));
        backgroundPaint.setAntiAlias(true);
        c.drawCircle(circleDiameter / 2, circleDiameter / 2, circleDiameter / 2, backgroundPaint);
        c.drawBitmap(largeIcon, padding, padding, null);
        return finalBitmap;
    }

    protected String getNotificationTimeString(Context c) {
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(c);
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(c);
        if (notificationTime == null) return "";
        return dateFormat.format(notificationTime) + " " + timeFormat.format(notificationTime);
    }
}
