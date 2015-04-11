package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewDistrictActivity;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.gcm.GCMMessageHandler;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.listeners.NotificationDismissedListener;
import com.thebluealliance.androidclient.models.StoredNotification;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by phil on 11/21/14.
 */
public class DistrictPointsUpdatedNotification extends BaseNotification {

    private String districtName, districtKey;

    public DistrictPointsUpdatedNotification(String messageData) {
        super(NotificationTypes.DISTRICT_POINTS_UPDATED, messageData);

    }

    @Override
    public void parseMessageData() throws JsonParseException {
        JsonObject jsonData = JSONManager.getasJsonObject(messageData);
        districtName = jsonData.get("district_name").getAsString();
        districtKey = jsonData.get("district_key").getAsString();
    }

    @Override
    public Notification buildNotification(Context context) {
        Resources r = context.getResources();

        String contentText = String.format(r.getString(R.string.notification_district_points_updated), districtName);

        Intent instance = ViewDistrictActivity.newInstance(context, districtKey);
        PendingIntent intent = PendingIntent.getActivity(context, getNotificationId(), instance, 0);
        PendingIntent onDismiss = PendingIntent.getBroadcast(context, 0, new Intent(context, NotificationDismissedListener.class), 0);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        String districtCode = EventHelper.getEventCode(districtKey);
        String title = r.getString(R.string.notification_district_points_title, districtCode);
        stored.setTitle(title);
        stored.setBody(contentText);
        stored.setIntent(MyTBAHelper.serializeIntent(instance));
        stored.setTime(Calendar.getInstance().getTime());
        
        NotificationCompat.Builder builder = getBaseBuilder(context)
                .setContentTitle(title)
                .setContentText(contentText)
                .setLargeIcon(getLargeIconFormattedForPlatform(context, R.drawable.ic_info_outline_white_24dp))
                .setContentIntent(intent)
                .setDeleteIntent(onDismiss)
                .setGroup(GCMMessageHandler.GROUP_KEY)
                .setAutoCancel(true)
                .extend(new NotificationCompat.WearableExtender().setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.tba_blue_background)));

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(contentText);
        builder.setStyle(style);
        return builder.build();
    }

    @Override
    public void updateDataLocally(Context c) {
        /* This notification has no data that we can store locally */
    }

    @Override
    public int getNotificationId() {
        return (new Date().getTime() + ":" + getNotificationType() + ":" + districtKey).hashCode();
    }

}
