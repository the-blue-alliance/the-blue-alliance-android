package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.renderers.MatchRenderer;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class RecentNotificationsSubscriber extends BaseAPISubscriber<List<StoredNotification>, List<Object>> {

    private final DatabaseWriter mWriter;
    private final MatchRenderer mMatchRenderer;
    private Context mContext;

    @Inject
    public RecentNotificationsSubscriber(DatabaseWriter writer, Context context, MatchRenderer matchRenderer) {
        super();
        mWriter = writer;
        mContext = context;
        mMatchRenderer = matchRenderer;
        mDataToBind = new ArrayList<>();
    }

    @Override
    public void parseData()  {
        mDataToBind.clear();
        for (int i = 0; i < mAPIData.size(); i++) {
            StoredNotification notification = mAPIData.get(i);
            BaseNotification renderable = notification.getNotification(mWriter, mMatchRenderer);
            if (renderable != null) {
                renderable.parseMessageData();
                Object viewModel = renderable.renderToViewModel(mContext, null);
                if (viewModel == null) {
                    TbaLogger.w("Attempt to bind to a null ViewModel from "
                            + notification.getType());
                } else {
                    mDataToBind.add(viewModel);
                }
            }
        }
    }

    @Override
    public boolean isDataValid() {
        return super.isDataValid() && !mAPIData.isEmpty();
    }
}
