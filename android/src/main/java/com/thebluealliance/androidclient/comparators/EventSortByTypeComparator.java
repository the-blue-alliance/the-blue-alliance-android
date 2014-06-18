package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.models.Event;

import java.util.Comparator;

/**
 * File created by phil on 5/7/14.
 */
public class EventSortByTypeComparator implements Comparator<Event> {
    @Override
    public int compare(Event event, Event event2) {
        int typeCompare = event.getEventType().compareTo(event2.getEventType());
        if (typeCompare == 0) {
            return ((Integer) event.getDistrictEnum()).compareTo(event2.getDistrictEnum());
        } else {
            return typeCompare;
        }
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
