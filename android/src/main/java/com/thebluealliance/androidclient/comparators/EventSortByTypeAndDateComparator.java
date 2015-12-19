package com.thebluealliance.androidclient.comparators;

import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.types.EventType;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;

import java.util.Comparator;

public class EventSortByTypeAndDateComparator implements Comparator<Event> {
    @Override
    public int compare(Event event, Event event2) {
        // Preseason < regional < district < district_cmp < cmp_division < cmp_finals < offseason
        try {
            if (event.getEventType() == event2.getEventType()) {
                int districtSort = ((Integer) event.getDistrictEnum()).compareTo(event2.getDistrictEnum());
                if (districtSort == 0) {
                    int eventSort = event.getStartDate().compareTo(event2.getStartDate());
                    if (eventSort == 0) {
                        return event.getEventShortName().compareTo(event2.getEventShortName());
                    } else {
                        return eventSort;
                    }
                } else {
                    return districtSort;
                }
            } else {
                int typeCompare = event.getEventType().compareTo(event2.getEventType());
                if (typeCompare == 0 && event.getEventType() == EventType.DISTRICT) {
                    return ((Integer) event.getDistrictEnum()).compareTo(event2.getDistrictEnum());
                } else {
                    return typeCompare;
                }
            }
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Can't compare events with missing fields." + e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
