package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.types.EventType;

import java.util.Comparator;

public class EventSortByTypeAndDateComparator implements Comparator<Event> {
    @Override
    public int compare(Event event, Event event2) {
        // Preseason < regional < district < district_cmp < cmp_division < cmp_finals < offseason
        if (event.getEventTypeEnum() == event2.getEventTypeEnum()) {
            int districtSort = (event.getEventDistrict()).compareTo(event2.getEventDistrict());
            if (districtSort == 0) {
                int eventSort = event.getStartDate().compareTo(event2.getStartDate());
                if (eventSort == 0) {
                    return event.getShortName().compareTo(event2.getShortName());
                } else {
                    return eventSort;
                }
            } else {
                return districtSort;
            }
        } else {
            int typeCompare = event.getEventType().compareTo(event2.getEventType());
            if (typeCompare == 0 && event.getEventTypeEnum() == EventType.DISTRICT) {
                return (event.getEventDistrict()).compareTo(event2.getEventDistrict());
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
