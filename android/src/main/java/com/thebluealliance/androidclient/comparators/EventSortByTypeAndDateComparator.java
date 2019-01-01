package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.types.EventType;

import java.util.Comparator;

public class EventSortByTypeAndDateComparator implements Comparator<Event> {
    @Override
    public int compare(Event event, Event event2) {
        // Preseason < regional < district < district_cmp < cmp_division < cmp_finals < offseason
        if (event.getEventTypeEnum() == event2.getEventTypeEnum()) {
            int districtSort = compareDistricts(event, event2);
            if (districtSort == 0) {
                int eventSort = compareStartDates(event, event2);
                if (eventSort == 0) {
                    return compareShortNames(event, event2);
                } else {
                    return eventSort;
                }
            } else {
                return districtSort;
            }
        } else {
            EventType type1 = event.getEventTypeEnum();
            EventType type2 = event2.getEventTypeEnum();
            int typeCompare = type1.getSortOrder() - type2.getSortOrder();
            if (typeCompare == 0 && event.getEventTypeEnum() == EventType.DISTRICT) {
                return compareDistricts(event, event2);
            } else {
                return typeCompare;
            }
        }
    }

    private int compareDistricts(Event event, Event event2) {
        if (event.getDistrictKey() == null || event2.getDistrictKey() == null) {
            return 0;
        }

        return (event.getDistrictKey()).compareTo(event2.getDistrictKey());
    }

    private int compareStartDates(Event event, Event event2) {
        if (event.getStartDate() == null || event2.getStartDate() == null) {
            return 0;
        }
        return event.getStartDate().compareTo(event2.getStartDate());
    }

    private int compareShortNames(Event event, Event event2) {
        if (event.getShortName() == null || event2.getShortName() == null) {
            return 0;
        }
        return event.getShortName().compareTo(event2.getShortName());
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
