package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * File created by phil on 10/12/14.
 */
public class CompLevelStartingNotification extends BaseNotification {

    JsonObject jsonData;

    public CompLevelStartingNotification(String messageData){
        super(NotificationTypes.LEVEL_STARTING, messageData);

        jsonData = new JsonParser().parse(messageData).getAsJsonObject();
    }

    @Override
    public Notification buildNotification(Context context) {
        Resources r = context.getResources();

        String eventName = jsonData.get("event_name").getAsString();
        String eventKey = jsonData.get("event_key").getAsString();
        String compLevelAbbrev = jsonData.get("comp_level").getAsString();
        String compLevel;
        switch (compLevelAbbrev){
            case "qm":  compLevel = r.getString(R.string.quarters_header); break;
            case "ef":  compLevel = r.getString(R.string.eigths_header); break;
            case "qf":  compLevel = r.getString(R.string.quarters_header); break;
            case "sf":  compLevel = r.getString(R.string.semis_header); break;
            case "f":   compLevel = r.getString(R.string.finals_header); break;
            default:    compLevel = ""; break;
        }
        String scheduledStartTimeString;
        if(jsonData.get("scheduled_time").isJsonNull()){
            scheduledStartTimeString = "";
        }else{
            long scheduledStartTimeUNIX = jsonData.get("scheduled_time").getAsLong();
            // We multiply by 1000 because the Date constructor expects
            Date scheduledStartTime = new Date(scheduledStartTimeUNIX * 1000);
            DateFormat format = new SimpleDateFormat("HH:mm");
            format.setTimeZone(TimeZone.getDefault());
            scheduledStartTimeString = format.format(scheduledStartTime);
        }

        String contentText;
        if(scheduledStartTimeString.isEmpty()){
            contentText = String.format(r.getString(R.string.notification_level_starting), eventName, compLevel);
        }else{
            contentText = String.format(r.getString(R.string.notification_level_starting_with_time), eventKey, compLevel, scheduledStartTimeString);
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
