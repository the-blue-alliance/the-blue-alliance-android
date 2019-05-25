package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.content.Context;
import androidx.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.gcm.FollowsChecker;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.viewmodels.GenericNotificationViewModel;

public class EventDownNotification extends GenericNotification {

    String eventKey;
    String eventName;

    public EventDownNotification(String messageData) {
        super(NotificationTypes.EVENT_DOWN, messageData);
    }

    @Override
    public void parseMessageData() throws JsonParseException {
        JsonObject jsonData = JSONHelper.getasJsonObject(messageData);
        if (!jsonData.has("event_key")) {
            throw new JsonParseException("Notification data does not contain 'event_key'");
        }
        eventKey = jsonData.get("event_key").getAsString();
        eventName = jsonData.has("event_name")
                ? jsonData.get("event_name").getAsString()
                : jsonData.get("event_key").getAsString();
    }

    @Override
    public Notification buildNotification(Context context, FollowsChecker followsChecker) {
        String eventCode = EventHelper.getEventCode(eventKey);
        title = context.getString(R.string.notification_event_down, eventCode);
        message = context.getString(R.string.notification_event_down_content, eventName);

        return super.buildNotification(context, followsChecker);
    }

    @Nullable
    @Override
    public GenericNotificationViewModel renderToViewModel(Context context, @Nullable Void aVoid) {
        String header = getNotificationCardHeader(context, eventName, eventKey);
        String eventCode = EventHelper.getEventCode(eventKey);
        title = context.getString(R.string.notification_event_down, eventCode);
        message = context.getString(R.string.notification_event_down_content, eventName);
        return new GenericNotificationViewModel(header, title, message, getNotificationTimeString(context), getIntent(context));
    }
}
