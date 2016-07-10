package com.thebluealliance.androidclient.database.writers;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.datafeed.KeyAndJson;
import com.thebluealliance.androidclient.models.Event;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EventStatsWriter extends BaseDbWriter<KeyAndJson> {

    private final EventWriter mEventWriter;

    @Inject
    public EventStatsWriter(Database db, BriteDatabase briteDb, EventWriter eventWriter) {
        super(db, briteDb);
        mEventWriter = eventWriter;
    }

    @Override
    public void write(KeyAndJson newData) {
        BriteDatabase.Transaction transaction = mBriteDb.newTransaction();
        try {
            Event event = mDb.getEventsTable().get(newData.key);
            if (event != null && newData.json != null && newData.json.isJsonObject()) {
                event.setStats(newData.json.getAsJsonObject());
                mEventWriter.write(event);
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }
}
