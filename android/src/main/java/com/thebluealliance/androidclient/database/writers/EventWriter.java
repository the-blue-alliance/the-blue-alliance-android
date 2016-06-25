package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Event;

import android.support.annotation.WorkerThread;

import javax.inject.Inject;

public class EventWriter extends BaseDbWriter<Event> {
    @Inject
    public EventWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(Event event) {
        mDb.getEventsTable().add(event);
    }
}