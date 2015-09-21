package com.thebluealliance.androidclient.database.writers;

import com.google.common.collect.ImmutableList;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Event;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class EventListWriter implements Action1<List<Event>> {
    private Database mDb;

    @Inject
    public EventListWriter(Database db) {
        mDb = db;
    }

    @Override
    public void call(List<Event> events) {
        Schedulers.io().createWorker()
          .schedule(() -> mDb.getEventsTable().add(ImmutableList.copyOf(events)));
    }
}