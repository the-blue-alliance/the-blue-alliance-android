package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Event;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class EventWriter implements Action1<Event> {
    private Database mDb;

    @Inject
    public EventWriter(Database db) {
        mDb = db;
    }

    @Override
    public void call(Event event) {
        Schedulers.io().createWorker().schedule(() -> mDb.getEventsTable().add(event));
    }
}