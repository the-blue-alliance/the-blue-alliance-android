package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.gcm.GCMMessageHandler;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.listeners.NotificationDismissedListener;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.StoredNotification;

import java.util.Calendar;
import java.util.Date;

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
            return null;
        }

        String contentText = String.format(r.getString(R.string.notification_alliances_updated), eventName);

        Intent instance = ViewEventActivity.newInstance(context, eventKey, ViewEventFragmentPagerAdapter.TAB_ALLIANCES);
        PendingIntent intent = PendingIntent.getActivity(context, getNotificationId(), instance, 0);
        PendingIntent onDismiss = PendingIntent.getBroadcast(context, 0, new Intent(context, NotificationDismissedListener.class), 0);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        String eventCode = EventHelper.getEventCode(eventKey);
        String title = r.getString(R.string.notification_alliances_updated_title, eventCode);
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
        if(event != null) {
            event.write(c);
        }
    }

    @Override
    public int getNotificationId() {
        return (new Date().getTime() + ":" + getNotificationType() + ":" + eventKey).hashCode();
    }

}
