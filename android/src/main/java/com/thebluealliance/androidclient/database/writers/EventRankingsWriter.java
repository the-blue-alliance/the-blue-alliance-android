package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.datafeed.KeyAndJson;
import com.thebluealliance.androidclient.models.Event;

import javax.inject.Inject;

public class EventRankingsWriter extends BaseDbWriter<KeyAndJson> {

    private final EventWriter mEventWriter;

    @Inject
    public EventRankingsWriter(Database db, EventWriter eventWriter) {
        super(db);
        mEventWriter = eventWriter;
    }

    @Override
    public void write(KeyAndJson newData) {
        mDb.getWritableDatabase().beginTransaction();
        try {
            Event event = mDb.getEventsTable().get(newData.key);
            if (event != null && newData.json != null && newData.json.isJsonArray()) {
                event.setRankings(newData.json.getAsJsonArray());
                mEventWriter.write(event);
            }
            mDb.getWritableDatabase().setTransactionSuccessful();
        } finally {
            mDb.getWritableDatabase().endTransaction();
        }
    }
}
