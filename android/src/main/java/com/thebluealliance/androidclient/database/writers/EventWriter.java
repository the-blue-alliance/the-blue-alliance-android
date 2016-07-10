package com.thebluealliance.androidclient.database.writers;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Event;

import android.support.annotation.WorkerThread;

import javax.inject.Inject;

public class EventWriter extends BaseDbWriter<Event> {
    @Inject
    public EventWriter(Database db, BriteDatabase briteDb) {
        super(db, briteDb);
    }

    @Override
    @WorkerThread
    public void write(Event event) {
        mDb.getEventsTable().add(event);
    }
}