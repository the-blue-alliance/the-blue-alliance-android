package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.gcm.GCMMessageHandler;

/**
 * File created by phil on 9/7/14.
 */
public class GenericNotification extends BaseNotification {

    private String title, message;
    private PendingIntent intent;
    private Context context;

    public GenericNotification(Context c, String type, String messageData){
        super(type, messageData);
        context = c;
    }

    @Override
    public void parseMessageData() throws JsonParseException{
        JsonObject jsonData = jsonData = JSONManager.getasJsonObject(messageData);
        if(!jsonData.has("title")){
            throw new JsonParseException("Notification data does not contain 'title'");
        }
        title = jsonData.get("title").getAsString();
        if(!jsonData.has("desc")){
            throw new JsonParseException("Notification data does not contain 'desc'");
        }
        message = jsonData.get("desc").getAsString();

        if(jsonData.has("url")){
            Uri uri = Uri.parse(jsonData.get("url").getAsString());
            Intent launch = Utilities.getIntentForTBAUrl(context, uri);
            launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent = PendingIntent.getActivity(context, 0, launch, 0);
        }else{
            intent = null;
        }

        if(jsonData.has("app_version")){
            String version = jsonData.get("app_version").getAsString();
            if(!version.equals(BuildConfig.VERSION_NAME)){
                // The broadcast is not targeted at this version, don't show it
                display = false;
            }
        }
    }


    @Override
    public Notification buildNotification(Context context) {
        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(intent)
                .setGroup(GCMMessageHandler.GROUP_KEY)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .extend(new NotificationCompat.WearableExtender().setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.tba_blue_background)))
                .build();
    }

    @Override
    public void updateDataLocally(Context c) {
        /* No data to be stored locally */
    }

    @Override
    public int getNotificationId() {
        return (getNotificationType() + ":" + messageData).hashCode();
    }
}
