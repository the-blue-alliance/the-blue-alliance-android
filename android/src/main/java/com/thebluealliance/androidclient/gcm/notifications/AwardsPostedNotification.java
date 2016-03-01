package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.database.writers.AwardListWriter;
import com.thebluealliance.androidclient.helpers.AwardHelper;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.listeners.GamedayTickerClickListener;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.viewmodels.AwardsPostedNotificationViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AwardsPostedNotification extends BaseNotification<AwardsPostedNotificationViewModel> {

    private final AwardListWriter mWriter;
    private String eventName, eventKey;
    private List<Award> awards;

    public AwardsPostedNotification(String messageData, AwardListWriter writer) {
        super(NotificationTypes.AWARDS, messageData);
        awards = new ArrayList<>();
        mWriter = writer;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventKey() {
        return eventKey;
    }

    public List<Award> getAwards() {
        return awards;
    }

    @Override
    public void parseMessageData() throws JsonParseException {
        JsonObject jsonData = JSONHelper.getasJsonObject(messageData);
        if (!jsonData.has("event_key")) {
            throw new JsonParseException("Notification data does not contain 'event_key'");
        }
        eventKey = jsonData.get("event_key").getAsString();
        if (!jsonData.has("event_name")) {
            throw new JsonParseException("Notification data does not contain 'event_name'");
        }
        eventName = jsonData.get("event_name").getAsString();
        if (!jsonData.has("awards") || !jsonData.get("awards").isJsonArray()) {
            throw new JsonParseException("Notification data does not contain 'awards' list");
        }
        JsonArray awardArray = jsonData.get("awards").getAsJsonArray();
        for (JsonElement element : awardArray) {
            awards.add(gson.fromJson(element, Award.class));
        }
    }

    @Override
    public Notification buildNotification(Context context) {
        String eventShortName = EventHelper.shortName(eventName);
        String contentText = context.getString(R.string.notification_awards_updated, eventShortName);

        Intent instance = getIntent(context);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        String eventCode = EventHelper.getEventCode(eventKey);
        String title = context.getString(R.string.notification_awards_updated_title, eventCode);
        stored.setTitle(title);
        stored.setBody(contentText);
        stored.setMessageData(messageData);
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
    public void updateDataLocally() {
        if (awards != null) {
            // Set award keys for writing
            for (Award award : awards) {
                try {
                    award.setKey(AwardHelper.createAwardKey(award.getEventKey(), award.getEnum()));
                } catch (BasicModel.FieldNotDefinedException e) {
                    /* Can't write without a key */
                }
            }
            mWriter.write(awards);
        }
    }

    @Override
    public Intent getIntent(Context context) {
        return ViewEventActivity.newInstance(context, eventKey, ViewEventFragmentPagerAdapter.TAB_AWARDS);
    }

    @Override
    public int getNotificationId() {
        return (new Date().getTime() + ":" + getNotificationType() + ":" + eventKey).hashCode();
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_notification_awards_posted, null, false);

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
        holder.details.setText(c.getString(R.string.notification_awards_updated_gameday_details));
        holder.time.setText(getNotificationTimeString(c));
        holder.summaryContainer.setOnClickListener(new GamedayTickerClickListener(c, this));

        return convertView;
    }

    @Nullable
    @Override
    public AwardsPostedNotificationViewModel renderToViewModel(Context context, @Nullable Void aVoid) {
        return new AwardsPostedNotificationViewModel(eventKey, eventName, getNotificationTimeString(context), getIntent(context));
    }

    private class ViewHolder {
        public TextView header;
        public TextView details;
        public TextView time;
        public LinearLayout summaryContainer;
    }
}
