package com.thebluealliance.androidclient.datafeed.maps;

import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

public class WeekEventsExtractor implements Func1<List<Event>, List<Event>> {

    private final int mWeek;

    public WeekEventsExtractor(int week) {
        mWeek = week;
    }

    @Override
    public List<Event> call(List<Event> events) {
        List<Event> weekEvents = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            if (event.getWeek() != null && event.getWeek() == mWeek) {
                weekEvents.add(event);
            }
        }
        return weekEvents;
    }
}
