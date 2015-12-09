package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.StoredNotification;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class RecentNotificationsSubscriber extends BaseAPISubscriber<List<StoredNotification>, List<ListItem>> {

    private final DatabaseWriter mWriter;

    @Inject
    public RecentNotificationsSubscriber(DatabaseWriter writer) {
        super();
        mWriter = writer;
        mDataToBind = new ArrayList<>();
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();

        if (mAPIData == null || mAPIData.isEmpty()) {
            return;
        }

        for (int i = 0; i < mAPIData.size(); i++) {
            StoredNotification notification = mAPIData.get(i);
            BaseNotification renderable = notification.getNotification(mWriter);
            if (renderable != null) {
                renderable.parseMessageData();
                mDataToBind.add(renderable);
            }
        }
    }
}
