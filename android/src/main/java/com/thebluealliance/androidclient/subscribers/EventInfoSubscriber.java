package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.binders.EventInfoBinder.Model;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;

public class EventInfoSubscriber extends BaseAPISubscriber<Event, Model> {

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind = new Model();
        if (mAPIData == null) {
            // no need to parse Event model
            return;
        }
        mDataToBind.eventKey = mAPIData.getKey();
        mDataToBind.nameString = mAPIData.getEventName();
        mDataToBind.actionBarTitle = mAPIData.getEventYear() + " " + mAPIData.getEventShortName();
        mDataToBind.venueString = mAPIData.getVenue();
        mDataToBind.locationString = mAPIData.getLocation();
        mDataToBind.eventWebsite = mAPIData.getWebsite();
        mDataToBind.dateString = mAPIData.getDateString();
        mDataToBind.isLive = mAPIData.isHappeningNow();
        mDataToBind.titleString = mAPIData.getEventYear() + " " + mAPIData.getEventShortName();
    }
}
