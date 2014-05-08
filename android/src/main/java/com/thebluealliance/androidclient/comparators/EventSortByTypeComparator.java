package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.models.Event;

import java.util.Comparator;

/**
 * File created by phil on 5/7/14.
 */
public class EventSortByTypeComparator implements Comparator<Event> {
    @Override
    public int compare(Event event, Event event2) {
        return event.getEventType().compareTo(event2.getEventType());
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
