package com.thebluealliance.androidclient.eventbus;

import com.thebluealliance.androidclient.models.Event;

/**
 * Created by Nathan on 8/15/2014.
 */
public class LiveEventEventUpdateEvent {

    private Event event;

    public LiveEventEventUpdateEvent(Event event){
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}
