package com.thebluealliance.androidclient.gcm.notifications;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.helpers.JSONHelper;

import android.content.Context;

public class EventDownNotification extends GenericNotification {

    public EventDownNotification(Context c, String messageData) {
        super(c, NotificationTypes.EVENT_DOWN, messageData);
    }

    @Override
    public boolean shouldShowInRecentNotificationsList() {
        return false;
    }

    @Override
    public void parseMessageData() throws JsonParseException {
        JsonObject jsonData = JSONHelper.getasJsonObject(messageData);
        if (!jsonData.has("event_key")) {
            throw new JsonParseException("Notification data does not contain 'event_key'");
        }
        String eventName = jsonData.has("event_name")
                ? jsonData.get("event_name").getAsString()
                : jsonData.get("event_key").getAsString();
        title = context.getString(R.string.notification_event_down);
        message = context.getString(R.string.notification_event_down_content, eventName);
    }
}
