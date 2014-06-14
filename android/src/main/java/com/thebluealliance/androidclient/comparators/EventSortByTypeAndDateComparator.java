package com.thebluealliance.androidclient.comparators;

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
            return event.getStartDate().compareTo(event2.getStartDate());
        } else {
            int typeCompare = event.getEventType().compareTo(event2.getEventType());
            if(typeCompare == 0 && event.getEventType() == Event.TYPE.DISTRICT){
                return event.getEventDistrict().compareTo(event2.getEventDistrict());
            }else{
                return typeCompare;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
