package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.comparators.EventSortByDateComparator;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventWeekTab;
import com.thebluealliance.androidclient.types.EventType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EventTabSubscriber extends BaseAPISubscriber<List<Event>, List<EventWeekTab>> {

    private Comparator<Event> mEventComparator;

    public EventTabSubscriber() {
        super();
        mDataToBind = new ArrayList<>();
        mEventComparator = new EventSortByDateComparator();
    }

    @Override
    public void parseData()  {
        mDataToBind.clear();
        Collections.sort(mAPIData, mEventComparator);

        Calendar cal = Calendar.getInstance();
        int lastEventWeek = -1;
        int lastEventMonth = -1;
        for (int i = 0; i < mAPIData.size(); i++) {
            Event event = mAPIData.get(i);
            cal.setTime(event.getFormattedStartDate());
            int competitionWeek = event.getCompetitionWeek() != null
                    ? event.getCompetitionWeek()
                    : 0;
            int month = cal.get(Calendar.MONTH);

            boolean isOffseason = event.getEventTypeEnum() == EventType.OFFSEASON;

            if (isOffseason ? lastEventMonth != month : lastEventWeek != competitionWeek) {
                mDataToBind.add(new EventWeekTab(
                  competitionWeek,
                  isOffseason ? month : -1,
                  EventHelper.generateLabelForEvent(event)));

                lastEventMonth = month;
                lastEventWeek = competitionWeek;
            }
        }
    }

    @Override public boolean isDataValid() {
        return super.isDataValid() && !mAPIData.isEmpty();
    }
}
