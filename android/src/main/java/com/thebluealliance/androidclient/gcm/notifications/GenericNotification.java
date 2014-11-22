package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 9/7/14.
 */
public class GenericNotification extends BaseNotification{

    private JsonObject jsonData;

    public GenericNotification(String messageData){
        super("generic", messageData);
        jsonData = new JsonParser().parse(messageData).getAsJsonObject();
    }

    @Override
    public Notification buildNotification(Context context) {
        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(jsonData.get("title").getAsString())
                .setContentText(jsonData.get("desc").getAsString())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(jsonData.get("desc").getAsString()))
                .extend(new NotificationCompat.WearableExtender().setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.tba_blue_background)))
                .build();
    }

    @Override
    public int getNotificationId() {
        return (getNotificationType() + ":" + messageData).hashCode();
    }
}
