package com.thebluealliance.androidclient.gcm.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.gcm.FollowsChecker;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.viewmodels.EventMatchVideoViewModel;

import java.util.Calendar;

public class EventMatchVideoNotification extends BaseNotification<EventMatchVideoViewModel> {

    private String eventName;
    private String eventKey;

    public EventMatchVideoNotification(String messageData) {
        super(NotificationTypes.EVENT_MATCH_VIDEO, messageData);
    }

    @Override
    public void parseMessageData() throws JsonParseException {
        JsonObject jsonData = gson.fromJson(messageData, JsonObject.class);
        if (!jsonData.has("event_key")) {
            throw new JsonParseException("EventMatchVideoNotification has no event key");
        }
        if (!jsonData.has("event_name")) {
            throw new JsonParseException("EventMatchVideoNotification has no event name");
        }

        eventKey = jsonData.get("event_key").getAsString();
        eventName = jsonData.get("event_name").getAsString();
    }

    @Nullable @Override
    public EventMatchVideoViewModel renderToViewModel(Context context, @Nullable Void aVoid) {
        String header = getNotificationCardHeader(context,
                                                  EventHelper.shortName(eventName),
                                                  EventHelper.getShortCodeForEventKey(eventKey));
        return new EventMatchVideoViewModel();
    }

    @Override
    public void buildStoredNotification(Context context, NotificationCompat.Builder builder,
            FollowsChecker followsChecker) {
        Resources r = context.getResources();
        String eventCode = EventHelper.getEventCode(eventKey);
        String title = r.getString(R.string.notification_event_match_video, eventCode);
        String notificationBody = r.getString(R.string.notification_event_match_video_content,
                                              EventHelper.shortName(eventName));

        Intent instance = getIntent(context);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        stored.setTitle(title);
        stored.setBody(notificationBody);
        stored.setIntent(MyTBAHelper.serializeIntent(instance));
        stored.setTime(Calendar.getInstance().getTime());
        stored.setMessageData(messageData);

        setupBaseBuilder(context, builder, instance)
                .setOnlyAlertOnce(true)
                .setContentTitle(title)
                .setContentText(notificationBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBody));
    }

    @Override
    public void updateDataLocally() {
        // Nothing to update
    }

    @Override
    public int getNotificationId() {
        return (getNotificationType() + ":" + eventKey).hashCode();
    }

    @Override
    public Intent getIntent(Context c) {
        return ViewEventActivity.newInstance(c,
                                             eventKey,
                                             ViewEventFragmentPagerAdapter.TAB_MATCHES);
    }
}
