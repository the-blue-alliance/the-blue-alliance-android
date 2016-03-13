package com.thebluealliance.androidclient.binders;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.eventbus.NotificationsUpdatedEvent;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;

import android.util.Log;

public class RecentNotificationsListBinder extends RecyclerViewBinder {

    public void onEvent(NotificationsUpdatedEvent event) {
        Log.d(Constants.LOG_TAG, "Updating notification list");
        BaseNotification notification = event.getNotification();
        notification.parseMessageData();
        if (notification.shouldShowInRecentNotificationsList()) {
            mAdapter.addItem(notification.renderToViewModel(null, null));
        }
    }
}
