package com.thebluealliance.androidclient.database.writers;

import androidx.annotation.WorkerThread;

import com.google.common.collect.ImmutableList;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Event;

import java.util.List;

import javax.inject.Inject;

public class EventListWriter extends BaseDbWriter<List<Event>> {
    @Inject
    public EventListWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(List<Event> events, Long lastModified) {
        mDb.getEventsTable().add(ImmutableList.copyOf(events), lastModified);
    }
}