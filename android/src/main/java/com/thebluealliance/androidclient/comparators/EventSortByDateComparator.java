package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.models.Event;

import java.util.Comparator;

public class EventSortByDateComparator implements Comparator<Event> {
    @Override
    public int compare(Event event, Event event2) {
        int dateCompare = event.getStartDate().compareTo(event2.getStartDate());
        if (dateCompare == 0) {
            return event.getEndDate().compareTo(event2.getEndDate());
        } else {
            return dateCompare;
        }
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
