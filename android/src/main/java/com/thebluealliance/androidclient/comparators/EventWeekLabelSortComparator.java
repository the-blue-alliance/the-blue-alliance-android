package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.models.Event;

import java.util.Comparator;

/**
 * File created by phil on 5/21/14.
 */
public class EventWeekLabelSortComparator implements Comparator<String>{

    static EventTypeComparator typeSort = new EventTypeComparator();

    @Override
    public int compare(String lhs, String rhs) {
        if (lhs.startsWith("Week") && rhs.startsWith("Week")) {
            return lhs.compareTo(rhs);
        } else {
            return typeSort.compare(Event.TYPE.fromLabel(lhs), Event.TYPE.fromLabel(rhs));
        }
    }
}

class EventTypeComparator implements Comparator<Event.TYPE>{

    @Override
    public int compare(Event.TYPE lhs, Event.TYPE rhs) {
        return ((Integer)Event.getEventOrder(lhs)).compareTo(Event.getEventOrder(rhs));
    }
}
