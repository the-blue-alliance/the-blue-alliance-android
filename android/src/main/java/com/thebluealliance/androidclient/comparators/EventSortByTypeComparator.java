package com.thebluealliance.androidclient.comparators;

import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;

import java.util.Arrays;
import java.util.Comparator;

/**
 * File created by phil on 5/7/14.
 */
public class EventSortByTypeComparator implements Comparator<Event> {
    @Override
    public int compare(Event event, Event event2) {
        try {
            int typeCompare = event.getEventType().compareTo(event2.getEventType());
            if (typeCompare == 0) {
                return ((Integer) event.getDistrictEnum()).compareTo(event2.getDistrictEnum());
            } else {
                return typeCompare;
            }
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Can't compare events with missing fields" +
                    Arrays.toString(e.getStackTrace()));
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
