package com.thebluealliance.androidclient.eventbus;

import com.thebluealliance.androidclient.models.Event;

public class LiveEventEventUpdateEvent {

    private Event event;

    public LiveEventEventUpdateEvent(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}
