package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;

import java.util.Comparator;

/**
 * File created by phil on 5/21/14.
 */
public class EventWeekComparator implements Comparator<Event> {

    @Override
    public int compare(Event lhs, Event rhs) {
        try {
            return Integer.compare(lhs.getCompetitionWeek(), rhs.getCompetitionWeek());
        } catch (BasicModel.FieldNotDefinedException e) {
            return 0;
        }
    }
}
