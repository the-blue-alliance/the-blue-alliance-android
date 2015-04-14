package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
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

import java.util.Calendar;
import java.util.Date;

/**
 * Created by phil on 11/21/14.
 */
public class ScheduleUpdatedNotification extends BaseNotification {

    private String eventName, eventKey;
    private JsonElement matchTime;

    public ScheduleUpdatedNotification(String messageData) {
        super(NotificationTypes.SCHEDULE_UPDATED, messageData);
    }

    @Override
    public void parseMessageData() throws JsonParseException {
        JsonObject jsonData = JSONManager.getasJsonObject(messageData);
        if (!jsonData.has("event_name")) {
            throw new JsonParseException("Notification data does not contain 'event_name");
        }
        eventName = jsonData.get("event_name").getAsString();
        if (!jsonData.has("event_key")) {
            throw new JsonParseException("Notification data does not contain 'event_key");
        }
        eventKey = jsonData.get("event_key").getAsString();

        if (!jsonData.has("first_match_time")) {
            throw new JsonParseException("Notification data does not contain 'first_match_time");
        }
        matchTime = jsonData.get("first_match_time");
    }

    @Override
    public Notification buildNotification(Context context) {
        Resources r = context.getResources();

        String firstMatchTime = null;
        if (!JSONManager.isNull(matchTime)) {
            Date date = new Date(matchTime.getAsLong() * 1000L);
            java.text.DateFormat format = DateFormat.getTimeFormat(context);
            firstMatchTime = format.format(date);
        }

        String contentText;
        if (firstMatchTime == null) {
            contentText = String.format(r.getString(R.string.notification_schedule_updated_without_time), eventName);
        } else {
            contentText = String.format(r.getString(R.string.notification_schedule_updated_with_time), eventName, firstMatchTime);
        }

        Intent instance = ViewEventActivity.newInstance(context, eventKey, ViewEventFragmentPagerAdapter.TAB_MATCHES);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        String eventCode = EventHelper.getEventCode(eventKey);
        String title = r.getString(R.string.notification_schedule_updated_title, eventCode);
        stored.setTitle(title);
        stored.setBody(contentText);
        stored.setIntent(MyTBAHelper.serializeIntent(instance));
        stored.setTime(Calendar.getInstance().getTime());
        
        NotificationCompat.Builder builder = getBaseBuilder(context, instance)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(getLargeIconFormattedForPlatform(context, R.drawable.ic_access_time_white_24dp));

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
        return (new Date().getTime() + ":" + getNotificationType() + ":" + eventKey).hashCode();
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_notification_schedule_updated, null, false);

            holder = new ViewHolder();
            holder.header = (TextView) convertView.findViewById(R.id.card_header);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.details = (TextView) convertView.findViewById(R.id.details);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        this.parseMessageData();

        String firstMatchTime = null;
        if (!JSONManager.isNull(matchTime)) {
            Date date = new Date(matchTime.getAsLong() * 1000L);
            java.text.DateFormat format = DateFormat.getTimeFormat(c);
            firstMatchTime = format.format(date);
        }

        holder.header.setText(eventName + " [" + EventHelper.getShortCodeForEventKey(eventKey).toUpperCase() + "]");
        holder.title.setText("Match Schedule Updated");
        holder.details.setText("Matches resume at "+firstMatchTime);

        return convertView;
    }

    private class ViewHolder {
        public TextView header;
        public TextView title;
        public TextView details;
    }
}
