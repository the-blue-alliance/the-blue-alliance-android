package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.StoredNotification;

import java.util.Calendar;

/**
 * Created by phil on 11/21/14.
 */
public class AllianceSelectionNotification extends BaseNotification{

    private Event event;
    private String eventKey;

    public AllianceSelectionNotification(String messageData){
        super(NotificationTypes.ALLIANCE_SELECTION, messageData);
    }

    @Override
    public void parseMessageData() throws JsonParseException{
        JsonObject jsonData = JSONManager.getasJsonObject(messageData);
        if(!jsonData.has("event")){
            throw new JsonParseException("Notification data does not have an 'event' object");
        }
        event = gson.fromJson(jsonData.get("event"), Event.class);
    }

    @Override
    public Notification buildNotification(Context context) {
        Resources r = context.getResources();
        String eventName = null;
        try {
            eventName = event.getEventShortName();
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(getLogTag(), "Event data passed in this notification does not contain an event short name. Can't post notification");
            e.printStackTrace();
            return null;
        }

        try {
            eventKey = event.getEventKey();
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(getLogTag(), "Event data passed in this notification does not contain an event short name. Can't post this notification.");
            e.printStackTrace();
        }

        String contentText = String.format(r.getString(R.string.notification_alliances_updated), eventName);

        Intent instance = ViewEventActivity.newInstance(context, eventKey);
        PendingIntent intent = PendingIntent.getActivity(context, 0, instance, 0);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        stored.setTitle(r.getString(R.string.notification_alliances_updated_title));
        stored.setBody(contentText);
        stored.setIntent(MyTBAHelper.serializeIntent(instance));
        stored.setTime(Calendar.getInstance().getTime());
        
        NotificationCompat.Builder builder = getBaseBuilder(context)
                .setContentTitle(r.getString(R.string.notification_alliances_updated_title))
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
        if(event != null) {
            event.write(c);
        }
    }

    @Override
    public int getNotificationId() {
        return (getNotificationType() + ":" + eventKey).hashCode();
    }

}
