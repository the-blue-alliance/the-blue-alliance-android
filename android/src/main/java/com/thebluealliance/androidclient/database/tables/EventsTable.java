package com.thebluealliance.androidclient.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;

import java.util.List;

public class EventsTable extends ModelTable<Event> {
    public static final String KEY = "key",
            YEAR = "year",
            NAME = "name",
            SHORTNAME = "shortName",
            LOCATION = "location",
            VENUE = "venue",
            TYPE = "eventType",
            DISTRICT = "eventDistrict",
            DISTRICT_STRING = "districtString",
            DISTRICT_POINTS = "districtPoints",
            START = "startDate",
            END = "endDate",
            OFFICIAL = "official",
            WEEK = "competitionWeek",
            TEAMS = "teams",
            RANKINGS = "rankings",
            ALLIANCES = "alliances",
            WEBCASTS = "webcasts",
            STATS = "stats",
            WEBSITE = "website";

    private SQLiteDatabase mDb;

    public EventsTable(SQLiteDatabase db) {
        super(db);
        mDb = db;
    }

    @Override
    protected void insertCallback(Event event) {
        ContentValues cv = new ContentValues();
        try {
            cv.put(Database.SearchEvent.KEY, event.getKey());
            cv.put(Database.SearchEvent.TITLES, Utilities.getAsciiApproximationOfUnicode(event.getSearchTitles()));
            cv.put(Database.SearchEvent.YEAR, event.getEventYear());
            mDb.insert(Database.TABLE_SEARCH_EVENTS, null, cv);

        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Can't insert event search item without the following fields:" +
                    "Database.Events.KEY, Database.Events.YEAR");
        } catch (SQLiteException e) {
            Log.w(Constants.LOG_TAG, "Trying to add a SearchEvent that already exists. " + event.getKey());
        }
    }

    @Override
    protected void updateCallback(Event event) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(Database.SearchEvent.KEY, event.getKey());
            cv.put(Database.SearchEvent.TITLES, Utilities.getAsciiApproximationOfUnicode(event.getSearchTitles()));
            cv.put(Database.SearchEvent.YEAR, event.getEventYear());

            mDb.update(Database.TABLE_SEARCH_EVENTS, cv, Database.SearchEvent.KEY + "=?", new String[]{event.getKey()});
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Can't insert event search item without the following fields:" +
                    "Database.Events.KEY, Database.Events.YEAR");
        }
    }

    @Override
    protected void deleteCallback(Event event) {
        mDb.delete(Database.TABLE_SEARCH_EVENTS, Database.SearchEvent.KEY + " = ?", new String[]{event.getKey()});
    }

    @Override
    public String getTableName() {
        return Database.TABLE_EVENTS;
    }

    @Override
    public String getKeyColumn() {
        return KEY;
    }

    @Override
    public Event inflate(Cursor cursor) {
        return ModelInflater.inflateEvent(cursor);
    }

    public void deleteAllSearchIndexes() {
        mDb.rawQuery("DELETE FROM " + getTableName(), new String[]{});
    }

    public void deleteSearchIndex(Event event) {
        deleteCallback(event);
    }

    public void recreateAllSearchIndexes(List<Event> events) {
        mDb.beginTransaction();
        try {
            for (int i = 0; i < events.size(); i++) {
                insertCallback(events.get(i));
            }
        } finally {
            mDb.setTransactionSuccessful();
        }
        mDb.endTransaction();
    }

}
