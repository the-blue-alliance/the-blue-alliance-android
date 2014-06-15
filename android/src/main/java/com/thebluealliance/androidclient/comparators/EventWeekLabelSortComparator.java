package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.helpers.EventHelper;

import java.util.Comparator;

/**
 * File created by phil on 5/21/14.
 */
public class EventWeekLabelSortComparator implements Comparator<String> {

    static EventTypeComparator typeSort = new EventTypeComparator();

    @Override
    public int compare(String lhs, String rhs) {
        if (lhs.startsWith("Week") && rhs.startsWith("Week")) {
            return lhs.compareTo(rhs);
        } else {
            return typeSort.compare(EventHelper.TYPE.fromLabel(lhs), EventHelper.TYPE.fromLabel(rhs));
        }
    }
}

class EventTypeComparator implements Comparator<EventHelper.TYPE> {

    @Override
    public int compare(EventHelper.TYPE lhs, EventHelper.TYPE rhs) {
        return ((Integer) EventHelper.getEventOrder(lhs)).compareTo(EventHelper.getEventOrder(rhs));
    }
}
