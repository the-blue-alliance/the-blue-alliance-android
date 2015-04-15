package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.StoredNotification;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by phil on 11/21/14.
 */
public class AwardsPostedNotification extends BaseNotification {

    private String eventName, eventKey;
    private List<Award> awards;

    public AwardsPostedNotification(String messageData){
        super(NotificationTypes.AWARDS, messageData);
        awards = new ArrayList<>();
    }

    @Override
    public void parseMessageData() throws JsonParseException{
        JsonObject jsonData = JSONManager.getasJsonObject(messageData);
        if(!jsonData.has("event_key")){
            throw new JsonParseException("Notification data does not contain 'event_key'");
        }
        eventKey = jsonData.get("event_key").getAsString();
        if(!jsonData.has("event_name")){
            throw new JsonParseException("Notification data does not contain 'event_name'");
        }
        eventName = jsonData.get("event_name").getAsString();
        if(!jsonData.has("awards") || !jsonData.get("awards").isJsonArray()){
            throw new JsonParseException("Notification data does not contain 'awards' list");
        }
        JsonArray awardArray = jsonData.get("awards").getAsJsonArray();
        for(JsonElement element: awardArray){
            awards.add(gson.fromJson(element, Award.class));
        }
    }

    @Override
    public Notification buildNotification(Context context) {
        String eventShortName = EventHelper.shortName(eventName);
        String contentText = context.getString(R.string.notification_awards_updated, eventShortName);

        Intent instance = ViewEventActivity.newInstance(context, eventKey, ViewEventFragmentPagerAdapter.TAB_AWARDS);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        String eventCode = EventHelper.getEventCode(eventKey);
        String title = context.getString(R.string.notification_awards_updated_title, eventCode);
        stored.setTitle(title);
        stored.setBody(contentText);
        stored.setIntent(MyTBAHelper.serializeIntent(instance));
        stored.setTime(Calendar.getInstance().getTime());
        
        NotificationCompat.Builder builder = getBaseBuilder(context, instance)
                .setContentTitle(title)
                .setContentText(contentText)
                .setLargeIcon(getLargeIconFormattedForPlatform(context, R.drawable.ic_assessment_white_24dp));

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(contentText);
        builder.setStyle(style);
        return builder.build();
    }

    @Override
    public void updateDataLocally(Context c) {
        for(Award award:awards){
            if(award != null) {
                award.write(c);
            }
        }
    }

    @Override
    public int getNotificationId() {
        return (new Date().getTime() + ":" + getNotificationType() + ":" + eventKey).hashCode();
    }
}
