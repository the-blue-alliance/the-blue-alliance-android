package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.comparators.EventSortByDateComparator;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventWeekTab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

        LinkedHashMap<String, EventWeekTab> eventTabs = new LinkedHashMap<>();
        for (Event event : mAPIData) {
            int eventWeek = event.getWeek() != null
                    ? event.getWeek()
                    : -1;
            String label = EventHelper.generateLabelForEvent(event);
            if (!eventTabs.containsKey(label)) {
                eventTabs.put(label, new EventWeekTab(label, eventWeek));
            }

            EventWeekTab tab = eventTabs.get(label);
            if (tab == null) {
                throw new RuntimeException("Expected to find event tab, but can't!");
            }
            tab.addEventKey(event.getKey());
        }

        for (Map.Entry<String, EventWeekTab> tab : eventTabs.entrySet()) {
            mDataToBind.add(tab.getValue());
        }
    }

    @Override public boolean isDataValid() {
        return super.isDataValid() && !mAPIData.isEmpty();
    }
}
