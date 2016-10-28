package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.datafeed.KeyAndJson;
import com.thebluealliance.androidclient.models.Event;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EventStatsWriter extends BaseDbWriter<KeyAndJson> {

    private final EventWriter mEventWriter;

    @Inject
    public EventStatsWriter(Database db, EventWriter eventWriter) {
        super(db);
        mEventWriter = eventWriter;
    }

    @Override
    public void write(KeyAndJson newData, Long lastModified) {
        mDb.getWritableDatabase().beginTransaction();
        try {
            Event event = mDb.getEventsTable().get(newData.key);
            if (event != null && newData.json != null && newData.json.isJsonObject()) {
                //TODO(773) Requires EventDetails
                //event.setStats(newData.json.getAsJsonObject());
                mEventWriter.write(event, lastModified);
            }
            mDb.getWritableDatabase().setTransactionSuccessful();
        } finally {
            mDb.getWritableDatabase().endTransaction();
        }
    }
}
