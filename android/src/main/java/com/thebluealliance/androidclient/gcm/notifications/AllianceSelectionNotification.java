package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.database.writers.EventWriter;
import com.thebluealliance.androidclient.gcm.FollowsChecker;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.viewmodels.AllianceSelectionNotificationViewModel;

import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class AllianceSelectionNotification extends BaseNotification<AllianceSelectionNotificationViewModel> {

    private final EventWriter mWriter;
    private final Gson mGson;
    private Event event;
    private String eventKey;

    public AllianceSelectionNotification(String messageData, EventWriter writer, Gson gson) {
        super(NotificationTypes.ALLIANCE_SELECTION, messageData);
        mWriter = writer;
        mGson = gson;
    }

    public Event getEvent() {
        return event;
    }

    public String getEventKey() {
        return eventKey;
    }

    @Override
    public void parseMessageData() throws JsonParseException {
        JsonObject jsonData = JSONHelper.getasJsonObject(messageData);
        if (!jsonData.has("event")) {
            throw new JsonParseException("Notification data does not have an 'event' object");
        }
        event = mGson.fromJson(jsonData.get("event"), Event.class);
        eventKey = event.getKey();
    }

    @Override
    public Notification buildNotification(Context context, FollowsChecker followsChecker) {
        Resources r = context.getResources();
        String eventName = event.getShortName();

        String contentText = r.getString(R.string.notification_alliances_updated, eventName);
        Intent instance = getIntent(context);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        String eventCode = EventHelper.getEventCode(eventKey);
        String title = r.getString(R.string.notification_alliances_updated_title, eventCode);
        stored.setTitle(title);
        stored.setBody(contentText);
        stored.setMessageData(messageData);
        stored.setIntent(MyTBAHelper.serializeIntent(instance));
        stored.setTime(Calendar.getInstance().getTime());
        stored.setSystemId(getNotificationId());

        NotificationCompat.Builder builder = getBaseBuilder(context, instance)
                .setContentTitle(title)
                .setContentText(contentText);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(contentText);
        builder.setStyle(style);
        return builder.build();
    }

    @Override
    public Intent getIntent(Context context) {
        return ViewEventActivity.newInstance(context, eventKey, ViewEventFragmentPagerAdapter.TAB_ALLIANCES);
    }

    @Override
    public void updateDataLocally() {
        if (event != null) {
            mWriter.write(event);
        }
    }

    @Override
    public int getNotificationId() {
        return (getNotificationType() + ":" + eventKey).hashCode();
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_notification_alliance_selection, null, false);

            holder = new ViewHolder();
            holder.header = (TextView) convertView.findViewById(R.id.card_header);
            holder.details = (TextView) convertView.findViewById(R.id.details);
            holder.time = (TextView) convertView.findViewById(R.id.notification_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String shortName = event.getShortName();
        String shortCode = EventHelper.getShortCodeForEventKey(event.getKey()).toUpperCase();
        String titleString = c.getString(R.string.gameday_ticker_event_title_format, shortName, shortCode);
        holder.header.setText(titleString);
        holder.details.setText(c.getString(R.string.notification_alliances_updated_gameday_details));
        holder.time.setText(getNotificationTimeString(c));

        return convertView;
    }

    @Nullable
    @Override
    public AllianceSelectionNotificationViewModel renderToViewModel(Context context, @Nullable Void aVoid) {
        String titleString = getNotificationCardHeader(context, event.getShortName(), event.getKey());

        return new AllianceSelectionNotificationViewModel(titleString, getNotificationTimeString(context), getIntent(context));
    }

    private static class ViewHolder {
        public TextView header;
        public TextView details;
        public TextView time;
    }

}
