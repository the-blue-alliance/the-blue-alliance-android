package com.thebluealliance.androidclient.eventbus;

import com.thebluealliance.androidclient.models.Event;

public class LiveEventUpdateEvent {

    private Event event;

    public LiveEventUpdateEvent(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}
