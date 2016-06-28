package com.thebluealliance.androidclient.database.writers;

import com.google.common.collect.ImmutableList;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Event;

import android.support.annotation.WorkerThread;

import java.util.List;

import javax.inject.Inject;

public class EventListWriter extends BaseDbWriter<List<Event>> {
    @Inject
    public EventListWriter(Database db, BriteDatabase briteDb) {
        super(db, briteDb);
    }

    @Override
    @WorkerThread
    public void write(List<Event> events) {
        mDb.getEventsTable().add(ImmutableList.copyOf(events));
    }
}