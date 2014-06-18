package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.models.Event;

import java.util.Comparator;

/**
 * Created by Nathan on 4/30/2014.
 */
public class EventSortByTypeAndDateComparator implements Comparator<Event> {
    @Override
    public int compare(Event event, Event event2) {
        // Preseason < regional < district < district_cmp < cmp_division < cmp_finals < offseason
        if (event.getEventType() == event2.getEventType()) {
            int districtSort = ((Integer) event.getDistrictEnum()).compareTo(event2.getDistrictEnum());
            if (districtSort == 0) {
                return event.getStartDate().compareTo(event2.getStartDate());
            } else {
                return districtSort;
            }
        } else {
            int typeCompare = event.getEventType().compareTo(event2.getEventType());
            if (typeCompare == 0 && event.getEventType() == EventHelper.TYPE.DISTRICT) {
                return ((Integer) event.getDistrictEnum()).compareTo(event2.getDistrictEnum());
            } else {
                return typeCompare;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
