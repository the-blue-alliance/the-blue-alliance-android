package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.comparators.EventWeekComparator;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventWeekTab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EventTabSubscriber extends BaseAPISubscriber<List<Event>, List<EventWeekTab>> {

    private Comparator<Event> mEventComparator;

    public EventTabSubscriber() {
        super();
        mDataToBind = new ArrayList<>();
        mEventComparator = new EventWeekComparator();
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        if (mAPIData.isEmpty()) {
            return;
        }
        Collections.sort(mAPIData, mEventComparator);

        int lastEventWeek = -1;
        for (int i = 0; i < mAPIData.size(); i++) {
            Event event = mAPIData.get(i);
            int competitionWeek = event.getCompetitionWeek();

            if (lastEventWeek != competitionWeek) {
                mDataToBind.add(
                  new EventWeekTab(competitionWeek, EventHelper.generateLabelForEvent(event)));
                lastEventWeek = competitionWeek;
            }
        }
    }
}
