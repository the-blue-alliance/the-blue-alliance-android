package com.thebluealliance.androidclient.eventbus;

import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;

/**
 * A {@link de.greenrobot.event.EventBus} event that tells receivers there is a new
 * notification available to display
 */
public class NotificationsUpdatedEvent {

    private final BaseNotification mNotification;

    public NotificationsUpdatedEvent(BaseNotification notification) {
        mNotification = notification;
    }

    public BaseNotification getNotification() {
        return mNotification;
    }
}
