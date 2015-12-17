package com.thebluealliance.androidclient.eventbus;

import com.thebluealliance.androidclient.models.Event;

public class EventInfoLoadedEvent {

    private Event event;

    public EventInfoLoadedEvent(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}
