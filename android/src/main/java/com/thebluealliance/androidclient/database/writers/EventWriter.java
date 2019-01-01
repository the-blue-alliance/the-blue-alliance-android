package com.thebluealliance.androidclient.database.writers;

import android.support.annotation.WorkerThread;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Event;

import javax.inject.Inject;

public class EventWriter extends BaseDbWriter<Event> {
    @Inject
    public EventWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(Event event, Long lastModified) {
        mDb.getEventsTable().add(event, lastModified);
    }
}