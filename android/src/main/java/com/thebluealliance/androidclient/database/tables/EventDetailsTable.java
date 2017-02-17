package com.thebluealliance.androidclient.database.tables;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.models.EventDetail;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EventDetailsTable extends ModelTable<EventDetail> {

    public static final String KEY = "key",
            EVENT_KEY = "event_key",
            DETAIL_TYPE = "detail_type",
            JSON_DATA = "json_data",
            LAST_MODIFIED = "last_modified";

    public EventDetailsTable(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public String getTableName() {
        return Database.TABLE_EVENTDETAILS;
    }

    @Override
    public String getKeyColumn() {
        return KEY;
    }

    @Override
    public String getLastModifiedColumn() {
        return LAST_MODIFIED;
    }

    @Override
    public EventDetail inflate(Cursor cursor) {
        return ModelInflater.inflateEventDetail(cursor);
    }
}
