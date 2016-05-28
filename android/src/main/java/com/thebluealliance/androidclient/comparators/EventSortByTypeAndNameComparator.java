package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.types.EventType;

import android.util.Log;

import java.util.Comparator;

public class EventSortByTypeAndNameComparator implements Comparator<Event> {
    @Override
    public int compare(Event event, Event event2) {
        // Preseason < regional < district < district_cmp < cmp_division < cmp_finals < offseason
        try {
            if (event.getEventType() == event2.getEventType()) {
                int districtSort = ((Integer) event.getDistrictEnum()).compareTo(event2.getDistrictEnum());
                int nameSort = event.getEventShortName().compareTo(event2.getEventShortName());
                if (districtSort == 0) {
                    return nameSort;
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
            Log.w(Constants.LOG_TAG, "Can't compare events with missing fields.");
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
