package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.models.StoredNotification;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * File created by phil on 10/12/14.
 */
public class CompLevelStartingNotification extends BaseNotification {

    private JsonObject jsonData;
    private String eventName, eventKey, compLevelAbbrev;

    public CompLevelStartingNotification(String messageData) {
        super(NotificationTypes.LEVEL_STARTING, messageData);
    }

    @Override
    public void parseMessageData() throws JsonParseException {
        jsonData = JSONManager.getasJsonObject(messageData);
        if (!jsonData.has("event_name")) {
            throw new JsonParseException("Notification data does not contain 'event_name'");
        }
        eventName = jsonData.get("event_name").getAsString();
        if (!jsonData.has("event_key")) {
            throw new JsonParseException("Notification data does not contain 'event_key'");
        }
        eventKey = jsonData.get("event_key").getAsString();
        if (!jsonData.has("comp_level")) {
            throw new JsonParseException("Notification data does not contain 'comp_level'");
        }
        compLevelAbbrev = jsonData.get("comp_level").getAsString();
    }

    @Override
    public Notification buildNotification(Context context) {
        Resources r = context.getResources();
        String compLevel = getCompLevelNameFromAbbreviation(context, compLevelAbbrev);
        String scheduledStartTimeString;
        JsonElement scheduledTime = jsonData.get("scheduled_time");
        if(JSONManager.isNull(scheduledTime)){
            scheduledStartTimeString = "";
        }else{
            long scheduledStartTimeUNIX = scheduledTime.getAsLong();
            // We multiply by 1000 because the Date constructor expects ms
            Date scheduledStartTime = new Date(scheduledStartTimeUNIX * 1000);
            DateFormat format = android.text.format.DateFormat.getTimeFormat(context);
            scheduledStartTimeString = format.format(scheduledStartTime);
        }

        String contentText;
        if (scheduledStartTimeString.isEmpty()) {
            contentText = String.format(r.getString(R.string.notification_level_starting), eventName, compLevel);
        } else {
            contentText = String.format(r.getString(R.string.notification_level_starting_with_time), eventName, compLevel, scheduledStartTimeString);
        }

        Intent instance = ViewEventActivity.newInstance(context, eventKey, ViewEventFragmentPagerAdapter.TAB_MATCHES);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        String eventCode = EventHelper.getEventCode(eventKey);
        String title = r.getString(R.string.notification_level_starting_title, eventCode);
        stored.setTitle(title);
        stored.setBody(contentText);
        stored.setIntent(MyTBAHelper.serializeIntent(instance));
        stored.setTime(Calendar.getInstance().getTime());
        
        NotificationCompat.Builder builder = getBaseBuilder(context, instance)
                .setContentTitle(title)
                .setContentText(contentText)
                .setLargeIcon(getLargeIconFormattedForPlatform(context, R.drawable.ic_access_time_white_24dp));

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(contentText);
        builder.setStyle(style);
        return builder.build();
    }

    private String getCompLevelNameFromAbbreviation(Context c, String abbreviation) {
        Resources r = c.getResources();

        String compLevel;
        switch (abbreviation) {
            case "qm":
                compLevel = r.getString(R.string.quals_header);
                break;
            case "ef":
                compLevel = r.getString(R.string.eigths_header);
                break;
            case "qf":
                compLevel = r.getString(R.string.quarters_header);
                break;
            case "sf":
                compLevel = r.getString(R.string.semis_header);
                break;
            case "f":
                compLevel = r.getString(R.string.finals_header);
                break;
            default:
                compLevel = "";
                break;
        }
        return compLevel;
    }

    @Override
    public void updateDataLocally(Context c) {
        /* This notification has no data that we can store locally */
    }

    @Override
    public int getNotificationId() {
        return (new Date().getTime() + ":" + getNotificationType() + ":" + jsonData.get("event_key").getAsString()).hashCode();
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_notification_comp_level_starting, null, false);

            holder = new ViewHolder();
            holder.header = (TextView) convertView.findViewById(R.id.card_header);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.time = (TextView) convertView.findViewById(R.id.notification_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        this.parseMessageData();

        holder.header.setText(c.getString(R.string.gameday_ticker_event_title_format, eventName, EventHelper.getShortCodeForEventKey(eventKey).toUpperCase()));
        holder.title.setText(c.getString(R.string.notification_level_starting_gameday_details, getCompLevelNameFromAbbreviation(c, compLevelAbbrev)));
        holder.time.setText(getNotificationTimeString(c));

        return convertView;
    }

    private class ViewHolder {
        public TextView header;
        public TextView title;
        public TextView time;
    }
}
