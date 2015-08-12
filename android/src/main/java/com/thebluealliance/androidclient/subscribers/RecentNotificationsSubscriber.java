package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.RecentNotificationListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.StoredNotification;

import java.util.ArrayList;
import java.util.List;

public class RecentNotificationsSubscriber extends BaseAPISubscriber<List<StoredNotification>, List<ListItem>> {

    public RecentNotificationsSubscriber() {
        super();
        mDataToBind = new ArrayList<>();
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();

        for (int i = 0; i < mAPIData.size(); i++) {
            StoredNotification notification = mAPIData.get(i);
            mDataToBind.add(new RecentNotificationListItem(notification.getTitle(), notification.getBody(), notification.getIntent()));
        }
    }
}
