package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.binders.EventInfoBinder.Model;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;

public class EventInfoSubscriber extends BaseAPISubscriber<Event, Model> {

    public EventInfoSubscriber() {
        mDataToBind = null;
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind = new Model();
        mDataToBind.eventKey = mAPIData.getKey();
        mDataToBind.nameString = mAPIData.getEventName();
        mDataToBind.actionBarTitle = mAPIData.getEventShortName();
        mDataToBind.actionBarSubtitle = String.valueOf(mAPIData.getEventYear());
        mDataToBind.venueString = mAPIData.getVenue();
        mDataToBind.locationString = mAPIData.getLocation();
        mDataToBind.eventWebsite = mAPIData.getWebsite();
        mDataToBind.dateString = mAPIData.getDateString();
        mDataToBind.isLive = mAPIData.isHappeningNow();
        mDataToBind.webcasts = mAPIData.getWebcasts();
    }
}
