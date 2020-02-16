package com.thebluealliance.androidclient.interfaces;

import com.thebluealliance.androidclient.models.Event;

import java.util.List;

public interface EventsParticipatedUpdate {
    void updateEventsParticipated(List<Event> events);
}
