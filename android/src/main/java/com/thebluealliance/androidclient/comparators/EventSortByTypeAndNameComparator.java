package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.types.EventType;

import java.util.Comparator;

public class EventSortByTypeAndNameComparator implements Comparator<Event> {
    @Override
    public int compare(Event event, Event event2) {
        // Preseason < regional < district < district_cmp < cmp_division < cmp_finals < offseason
        if (event.getEventTypeEnum() == event2.getEventTypeEnum()) {
            int districtSort = event.getEventDistrictEnum().compareTo(event2.getEventDistrictEnum());
            int nameSort = event.getShortName().compareTo(event2.getShortName());
            if (districtSort == 0) {
                return nameSort;
            } else {
                return districtSort;
            }
        } else {
            EventType type1 = event.getEventTypeEnum();
            EventType type2 = event2.getEventTypeEnum();
            int typeCompare = type1.getSortOrder() - type2.getSortOrder();
            if (typeCompare == 0 && event.getEventTypeEnum() == EventType.DISTRICT) {
                return event.getEventDistrictEnum().compareTo(event2.getEventDistrictEnum());
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
