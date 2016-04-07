package com.thebluealliance.androidclient.gcm.notifications;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.gcm.FollowsChecker;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.viewmodels.ScheduleUpdatedNotificationViewModel;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScheduleUpdatedNotification extends BaseNotification<ScheduleUpdatedNotificationViewModel> {

    private String eventName, eventKey;
    private JsonElement matchTime;

    public ScheduleUpdatedNotification(String messageData) {
        super(NotificationTypes.SCHEDULE_UPDATED, messageData);
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventKey() {
        return eventKey;
    }

    public JsonElement getMatchTime() {
        return matchTime;
    }

    @Override
    public void parseMessageData() throws JsonParseException {
        JsonObject jsonData = JSONHelper.getasJsonObject(messageData);
        if (!jsonData.has("event_name")) {
            throw new JsonParseException("Notification data does not contain 'event_name");
        }
        eventName = jsonData.get("event_name").getAsString();
        if (!jsonData.has("event_key")) {
            throw new JsonParseException("Notification data does not contain 'event_key");
        }
        eventKey = jsonData.get("event_key").getAsString();

        matchTime = jsonData.get("first_match_time");
    }

    @Override
    public Notification buildNotification(Context context, FollowsChecker followsChecker) {
        String firstMatchTime = null;
        if (!JSONHelper.isNull(matchTime)) {
            Date date = new Date(matchTime.getAsLong() * 1000L);
            java.text.DateFormat format = DateFormat.getTimeFormat(context);
            firstMatchTime = format.format(date);
        }

        String eventShortName = EventHelper.shortName(eventName);
        String contentText;
        if (firstMatchTime == null) {
            contentText = context.getString(R.string.notification_schedule_updated_without_time, eventShortName);
        } else {
            contentText = context.getString(R.string.notification_schedule_updated_with_time, eventShortName, firstMatchTime);
        }

        Intent instance = getIntent(context);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        String eventCode = EventHelper.getEventCode(eventKey);
        String title = context.getString(R.string.notification_schedule_updated_title, eventCode);
        stored.setTitle(title);
        stored.setBody(contentText);
        stored.setMessageData(messageData);
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
    public void updateDataLocally() {
        /* This notification has no data that we can store locally */
    }

    @Override
    public Intent getIntent(Context c) {
        return ViewEventActivity.newInstance(c, eventKey, ViewEventFragmentPagerAdapter.TAB_MATCHES);
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
            holder.details = (TextView) convertView.findViewById(R.id.details);
            holder.time = (TextView) convertView.findViewById(R.id.notification_time);
            holder.summaryContainer = (LinearLayout) convertView.findViewById(R.id.summary_container);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.header.setText(c.getString(R.string.gameday_ticker_event_title_format, EventHelper.shortName(eventName), EventHelper.getShortCodeForEventKey(eventKey).toUpperCase()));
        holder.details.setText(getNotificationBodyString(c));
        holder.time.setText(getNotificationTimeString(c));
        holder.summaryContainer.setOnClickListener(new GamedayTickerClickListener(c, this));

        return convertView;
    }

    @Nullable
    @Override
    public ScheduleUpdatedNotificationViewModel renderToViewModel(Context context, @Nullable Void aVoid) {
        String header = getNotificationCardHeader(context, EventHelper.shortName(eventName), EventHelper.getShortCodeForEventKey(eventKey));
        return new ScheduleUpdatedNotificationViewModel(header, getNotificationBodyString(context), getNotificationTimeString(context), getIntent(context));
    }

    private String getNotificationBodyString(Context context) {
        if (JSONHelper.isNull(matchTime)) {
            return context.getString(R.string.notification_schedule_updated_gameday_body_without_time);
        }

        Date date = new Date(matchTime.getAsLong() * 1000L);
        java.text.DateFormat timeFormat = DateFormat.getTimeFormat(context);
        String time = timeFormat.format(date);

        return context.getString(R.string.notification_schedule_updated_gameday_body_with_time, time);
    }

    private static class ViewHolder {
        public TextView header;
        public TextView details;
        public TextView time;
        public LinearLayout summaryContainer;
    }
}
