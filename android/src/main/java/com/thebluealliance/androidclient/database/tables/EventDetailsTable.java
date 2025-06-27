package com.thebluealliance.androidclient.database.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.models.EventDetail;

public class EventDetailsTable extends ModelTable<EventDetail> {

    public static final String KEY = "key",
            EVENT_KEY = "event_key",
            DETAIL_TYPE = "detail_type",
            JSON_DATA = "json_data";

    public EventDetailsTable(SQLiteDatabase db, Gson gson) {
        super(db, gson);
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
    public EventDetail inflate(Cursor cursor) {
        return ModelInflater.inflateEventDetail(cursor);
    }
}
