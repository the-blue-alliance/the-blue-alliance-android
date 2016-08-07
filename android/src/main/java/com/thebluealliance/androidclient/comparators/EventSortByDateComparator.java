package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;

import com.thebluealliance.androidclient.TbaLogger;

import java.util.Comparator;

public class EventSortByDateComparator implements Comparator<Event> {
    @Override
    public int compare(Event event, Event event2) {
        try {
            int dateCompare = event.getStartDate().compareTo(event2.getStartDate());
            if (dateCompare == 0) {
                return event.getEndDate().compareTo(event2.getEndDate());
            } else {
                return dateCompare;
            }
        } catch (BasicModel.FieldNotDefinedException e) {
            TbaLogger.e("Can't compare events with missing fields");
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
