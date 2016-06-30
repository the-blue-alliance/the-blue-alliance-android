package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.binders.EventInfoBinder.Model;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventInfo;
import com.thebluealliance.androidclient.models.Match;

import java.util.List;

public class EventInfoSubscriber extends BaseAPISubscriber<EventInfo, Model> {

    public EventInfoSubscriber() {
        mDataToBind = null;
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind = new Model();

        Event event = mAPIData.event;
        if (event != null) {
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
            mDataToBind.rankings = event.getRankings();
            mDataToBind.stats = event.getStats();
        }

        List<Match> matches = mAPIData.matches;
        if (matches != null) {
            MatchHelper.sortByPlayOrder(matches);
            mDataToBind.lastMatch = MatchHelper.getLastMatchPlayed(matches);
            mDataToBind.nextMatch = MatchHelper.getNextMatchPlayed(matches);
        }
    }

    @Override public boolean isDataValid() {
        return mAPIData != null && mAPIData.event != null;
    }
}
