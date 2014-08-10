package com.thebluealliance.androidclient.comparators;

import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;

import java.util.Comparator;

/**
 * File created by phil on 5/7/14.
 */
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
            Log.e(Constants.LOG_TAG, "Can't compare events with missing fields");
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
