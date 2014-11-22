package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.datafeed.JSONManager;

import java.util.Date;

/**
 * Created by phil on 11/21/14.
 */
public class ScheduleUpdatedNotification extends BaseNotification {

    JsonObject jsonData;

    public ScheduleUpdatedNotification(String messageData){
        super(NotificationTypes.SCHEDULE_POSTED, messageData);
        jsonData = JSONManager.getasJsonObject(messageData);
    }

    @Override
    public Notification buildNotification(Context context) {
        Resources r = context.getResources();

        String eventName = jsonData.get("event_name").getAsString();
        String eventKey = jsonData.get("event_key").getAsString();
        String firstMatchTime = null;
        if(!jsonData.get("first_match_time").isJsonNull()){
            Date date = new Date(jsonData.get("first_match_time").getAsLong() * 1000L);
            java.text.DateFormat format = DateFormat.getTimeFormat(context);
            firstMatchTime = format.format(date);
        }

        String contentText;
        if(firstMatchTime== null){
            contentText = String.format(r.getString(R.string.notification_schedule_updated_without_time), eventName);
        }else{
            contentText = String.format(r.getString(R.string.notification_schedule_updated_with_time), eventKey, firstMatchTime);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_time_light);
        PendingIntent intent = PendingIntent.getActivity(context, 0, ViewEventActivity.newInstance(context, eventKey), 0);

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
        return (getNotificationType() + ":" + jsonData.get("event_key").getAsString()).hashCode();
    }
}
