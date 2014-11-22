package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewDistrictActivity;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.datafeed.JSONManager;

/**
 * Created by phil on 11/21/14.
 */
public class DistrictPointsUpdatedNotification extends BaseNotification {

    private JsonObject jsonData;

    public DistrictPointsUpdatedNotification(String messageData){
        super(NotificationTypes.DISTRICT_POINTS_UPDATED, messageData);
        jsonData = JSONManager.getasJsonObject(messageData);
    }

    @Override
    public Notification buildNotification(Context context) {
        Resources r = context.getResources();
        String districtName = jsonData.get("district_name").getAsString();
        String districtKey = jsonData.get("district_key").getAsString();

        String contentText = String.format(r.getString(R.string.notification_district_points_updated), districtName);

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_info_outline_white_24dp);
        PendingIntent intent = PendingIntent.getActivity(context, 0, ViewDistrictActivity.newInstance(context, districtKey), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(r.getString(R.string.notification_level_starting_title))
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(largeIcon)
                .setContentIntent(intent)
                .setAutoCancel(true);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(contentText);
        builder.setStyle(style);
        return builder.build();
    }

    @Override
    public int getNotificationId() {
        return (getNotificationType() + ":" + jsonData.get("district_key").getAsString()).hashCode();
    }

}
