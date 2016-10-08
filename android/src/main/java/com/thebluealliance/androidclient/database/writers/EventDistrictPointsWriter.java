package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.datafeed.KeyAndJson;
import com.thebluealliance.androidclient.models.Event;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EventDistrictPointsWriter extends BaseDbWriter<KeyAndJson> {

    private final EventWriter mEventWriter;

    @Inject
    public EventDistrictPointsWriter(Database db, EventWriter eventWriter) {
        super(db);
        mEventWriter = eventWriter;
    }

    @Override
    public void write(KeyAndJson newData) {
        mDb.getWritableDatabase().beginTransaction();
        try {
            Event event = mDb.getEventsTable().get(newData.key);
            if (event != null && newData.json != null && newData.json.isJsonObject()) {
                //TODO(773) Requires EventDetails
                //event.setDistrictPoints(newData.json.getAsJsonObject().toString());
                mEventWriter.write(event);
            }
            mDb.getWritableDatabase().setTransactionSuccessful();
        } finally {
            mDb.getWritableDatabase().endTransaction();
        }
    }
}
