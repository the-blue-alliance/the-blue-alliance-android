package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
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
    private final Gson mGson;

    public EventMatchVideoNotification(String messageData, Gson gson) {
        super(NotificationTypes.EVENT_MATCH_VIDEO, messageData);
        mGson = gson;
    }

    @Override
    public void parseMessageData() throws JsonParseException {
        JsonObject jsonData = mGson.fromJson(messageData, JsonObject.class);
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
    public Notification buildNotification(Context context, FollowsChecker followsChecker) {
        Resources r = context.getResources();
        String eventCode = EventHelper.getEventCode(eventKey);
        String title = r.getString(R.string.notification_event_match_video, eventCode);
        String notificationBody = r.getString(R.string.notification_event_match_video_content,
                                              EventHelper.shortName(eventName));

        // We can finally build the notification!
        Intent instance = getIntent(context);

        stored = new StoredNotification();
        stored.setType(getNotificationType());
        stored.setTitle(title);
        stored.setBody(notificationBody);
        stored.setIntent(MyTBAHelper.serializeIntent(instance));
        stored.setTime(Calendar.getInstance().getTime());
        stored.setMessageData(messageData);
        stored.setSystemId(getNotificationId());

        NotificationCompat.Builder builder = getBaseBuilder(context, instance)
                .setOnlyAlertOnce(true)
                .setContentTitle(title)
                .setContentText(notificationBody);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(notificationBody);
        builder.setStyle(style);
        return builder.build();
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
