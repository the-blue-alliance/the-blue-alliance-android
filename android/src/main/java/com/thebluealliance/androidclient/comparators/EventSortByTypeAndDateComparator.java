package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.models.Event;

import java.util.Comparator;

/**
 * Created by Nathan on 4/30/2014.
 */
public class EventSortByTypeAndDateComparator implements Comparator<Event> {
    @Override
    public int compare(Event event, Event event2) {
        // Preseason < regional < district < district_cmp < cmp_division < cmp_finals < offseason
        if(event.getEventType() == event2.getEventType()) {
            return event.getStartDate().compareTo(event2.getStartDate());
        }else{
            return event.getEventType().compareTo(event2.getEventType());
        }
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
