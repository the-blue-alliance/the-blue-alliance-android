package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.binders.EventInfoBinder.Model;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventInfo;

public class EventInfoSubscriber extends BaseAPISubscriber<EventInfo, Model> {

    public EventInfoSubscriber() {
        mDataToBind = null;
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind = new Model();

        Event event = mAPIData.event;
        mDataToBind.eventKey = event.getKey();
        mDataToBind.nameString = event.getName();
        mDataToBind.actionBarTitle = event.getShortName();
        mDataToBind.actionBarSubtitle = String.valueOf(event.getYear());
        mDataToBind.venueString = event.getVenue();
        mDataToBind.locationString = event.getLocation();
        mDataToBind.eventWebsite = event.getWebsite();
        mDataToBind.dateString = event.getDateString();
        mDataToBind.isLive = event.isHappeningNow();
        mDataToBind.webcasts = event.getWebcasts();

        MatchHelper.sortByPlayOrder(mAPIData.matches);
        mDataToBind.lastMatch = MatchHelper.getLastMatchPlayed(mAPIData.matches);
        mDataToBind.nextMatch = MatchHelper.getNextMatchPlayed(mAPIData.matches);
    }
}
