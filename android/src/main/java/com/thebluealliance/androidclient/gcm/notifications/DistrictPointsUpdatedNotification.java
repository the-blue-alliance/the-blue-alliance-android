package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewDistrictActivity;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.models.StoredNotification;

import java.util.Calendar;

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

        PendingIntent intent = PendingIntent.getActivity(context, 0, ViewDistrictActivity.newInstance(context, districtKey), 0);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        stored.setTitle(r.getString(R.string.notification_district_points_title));
        stored.setBody(contentText);
        stored.setIntent(ViewDistrictActivity.newInstance(context, districtKey).toString());
        stored.setTime(Calendar.getInstance().getTime());
        
        NotificationCompat.Builder builder = getBaseBuilder(context)
                .setContentTitle(r.getString(R.string.notification_district_points_title))
                .setContentText(contentText)
                .setLargeIcon(getLargeIconFormattedForPlatform(context, R.drawable.ic_info_outline_white_24dp))
                .setContentIntent(intent)
                .setAutoCancel(true);

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
        return (getNotificationType() + ":" + districtKey).hashCode();
    }

}
