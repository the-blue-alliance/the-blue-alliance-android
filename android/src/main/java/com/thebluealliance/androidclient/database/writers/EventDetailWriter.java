package com.thebluealliance.androidclient.database.writers;

import android.support.annotation.WorkerThread;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.EventDetail;

import javax.inject.Inject;

public class EventDetailWriter extends BaseDbWriter<EventDetail> {

    @Inject
    public EventDetailWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(EventDetail eventDetail, Long lastModified) {
        mDb.getEventDetailsTable().add(eventDetail, lastModified);
    }
}
