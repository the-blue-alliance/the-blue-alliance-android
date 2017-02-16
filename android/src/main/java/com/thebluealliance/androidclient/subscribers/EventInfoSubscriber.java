package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.binders.EventInfoBinder.Model;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.models.Event;

public class EventInfoSubscriber extends BaseAPISubscriber<Event, Model> {

    public EventInfoSubscriber() {
        mDataToBind = null;
    }

    @Override
    public void parseData()  {
        mDataToBind = new Model();
        mDataToBind.eventKey = mAPIData.getKey();
        mDataToBind.nameString = mAPIData.getName();
        mDataToBind.actionBarTitle = mAPIData.getShortName();
        mDataToBind.actionBarSubtitle = String.valueOf(mAPIData.getYear());
        mDataToBind.venueString = mAPIData.getAddress();
        mDataToBind.locationString = mAPIData.getLocationName();
        mDataToBind.eventWebsite = mAPIData.getWebsite();
        mDataToBind.dateString = mAPIData.getDateString();
        mDataToBind.isLive = mAPIData.isHappeningNow();
        mDataToBind.webcasts = JSONHelper.getasJsonArray(mAPIData.getWebcasts());
    }
}
