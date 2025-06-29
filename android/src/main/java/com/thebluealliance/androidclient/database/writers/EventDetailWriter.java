package com.thebluealliance.androidclient.database.writers;

import androidx.annotation.WorkerThread;

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
    public void write(EventDetail eventDetail) {
        mDb.getEventDetailsTable().add(eventDetail);
    }
}
