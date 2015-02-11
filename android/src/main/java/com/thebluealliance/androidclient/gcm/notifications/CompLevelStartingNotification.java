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
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.gcm.GCMMessageHandler;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.listeners.NotificationDismissedListener;
import com.thebluealliance.androidclient.models.StoredNotification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * File created by phil on 10/12/14.
 */
public class CompLevelStartingNotification extends BaseNotification {

    private JsonObject jsonData;
    private String eventName, eventKey, compLevelAbbrev;

    public CompLevelStartingNotification(String messageData){
        super(NotificationTypes.LEVEL_STARTING, messageData);
    }

    @Override
    public void parseMessageData() throws JsonParseException{
        jsonData = JSONManager.getasJsonObject(messageData);
        if(!jsonData.has("event_name")){
            throw new JsonParseException("Notification data does not contain 'event_name'");
        }
        eventName = jsonData.get("event_name").getAsString();
        if(!jsonData.has("event_key")){
            throw new JsonParseException("Notification data does not contain 'event_key'");
        }
        eventKey = jsonData.get("event_key").getAsString();
        if(!jsonData.has("comp_level")){
            throw new JsonParseException("Notification data does not contain 'comp_level'");
        }
        compLevelAbbrev = jsonData.get("comp_level").getAsString();
    }

    @Override
    public Notification buildNotification(Context context) {
        Resources r = context.getResources();

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
        if(!jsonData.has("scheduled_time") || jsonData.get("scheduled_time").isJsonNull()){
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

        Intent instance = ViewEventActivity.newInstance(context, eventKey);
        PendingIntent intent = PendingIntent.getActivity(context, 0, instance, 0);
        PendingIntent onDismiss = PendingIntent.getBroadcast(context, 0, new Intent(context, NotificationDismissedListener.class), 0);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        stored.setTitle(r.getString(R.string.notification_level_starting_title));
        stored.setBody(contentText);
        stored.setIntent(MyTBAHelper.serializeIntent(instance));
        stored.setTime(Calendar.getInstance().getTime());
        
        NotificationCompat.Builder builder = getBaseBuilder(context)
                .setContentTitle(r.getString(R.string.notification_level_starting_title))
                .setContentText(contentText)
                .setLargeIcon(getLargeIconFormattedForPlatform(context, R.drawable.ic_access_time_white_24dp))
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
        return (getNotificationType() + ":" + jsonData.get("event_key").getAsString()).hashCode();
    }
}
