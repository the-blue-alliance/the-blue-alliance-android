package com.thebluealliance.androidclient.database.tables;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
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

    public EventsTable(SQLiteDatabase db, BriteDatabase briteDb) {
        super(db, briteDb);
    }

    /**
     * Generates a comma-separated list of all this model's columns, like the following:
     * <p>
     * event.key, events.name, events.year... and so on.
     * <p>
     * This is useful for when we only want to select the event table's columns during a join, it
     * saves us from having to type all this out in place.
     *
     * @return comma-separated list of this model's columns
     */
    public static String getAllColumnsForJoin() {
        List<String> columns = new ArrayList<>();
        columns.add(KEY);
        columns.add(YEAR);
        columns.add(NAME);
        columns.add(SHORTNAME);
        columns.add(LOCATION);
        columns.add(VENUE);
        columns.add(TYPE);
        columns.add(DISTRICT);
        columns.add(DISTRICT_STRING);
        columns.add(DISTRICT_POINTS);
        columns.add(START);
        columns.add(END);
        columns.add(OFFICIAL);
        columns.add(WEEK);
        columns.add(TEAMS);
        columns.add(RANKINGS);
        columns.add(ALLIANCES);
        columns.add(WEBCASTS);
        columns.add(STATS);
        columns.add(WEBSITE);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            builder.append(Database.TABLE_EVENTS);
            builder.append(".");
            builder.append(columns.get(i));
            if (i != columns.size() - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    @Override
    protected void insertCallback(Event event) {
        ContentValues cv = new ContentValues();
        try {
            cv.put(Database.SearchEvent.KEY, event.getKey());
            cv.put(Database.SearchEvent.TITLES, Utilities.getAsciiApproximationOfUnicode(event.getSearchTitles()));
            cv.put(Database.SearchEvent.YEAR, event.getYear());
            mDb.insert(Database.TABLE_SEARCH_EVENTS, null, cv);

        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Can't insert event search item without the following fields:"
                    + "Database.Events.KEY, Database.Events.YEAR");
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
            cv.put(Database.SearchEvent.YEAR, event.getYear());

            mDb.update(Database.TABLE_SEARCH_EVENTS, cv, Database.SearchEvent.KEY + "=?", new String[]{event.getKey()});
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Can't insert event search item without the following fields:"
                    + "Database.Events.KEY, Database.Events.YEAR");
        }
    }

    @Override
    protected void deleteCallback(Event event) {
        mDb.delete(Database.TABLE_SEARCH_EVENTS, Database.SearchEvent.KEY + " = ?", new String[]{event.getKey()});
    }

    @Override
    protected void deleteAllCallback() {
        mDb.execSQL("delete from " + Database.TABLE_SEARCH_EVENTS);
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

    /**
     * Used in {@link com.thebluealliance.androidclient.activities.MoreSearchResultsActivity}
     * If you change the ordering of the rows selected, be sure that you also updated indexes in
     * {@link com.thebluealliance.androidclient.adapters.EventCursorAdapter}
     */
    public Cursor getForSearchQuery(String query) {
        String table = getTableName();
        String searchTable = Database.TABLE_SEARCH_EVENTS;
        String rawQuery = "SELECT " + table + ".rowid as '_id',"
                + table + "." + KEY + ","
                + table + "." + NAME + ","
                + table + "." + SHORTNAME + ","
                + table + "." + TYPE + ","
                + table + "." + DISTRICT + ","
                + table + "." + START + ","
                + table + "." + END + ","
                + table + "." + LOCATION + ","
                + table + "." + VENUE + ","
                + table + "." + OFFICIAL + ","
                + table + "." + DISTRICT_STRING
                + " FROM " + getTableName()
                + " JOIN (SELECT " + searchTable + "." + Database.SearchEvent.KEY + " FROM " + searchTable + " WHERE " + searchTable + "." + Database.SearchEvent.TITLES + " MATCH ?)"
                + " as 'tempevents' ON tempevents." + Database.SearchEvent.KEY + " = " + table + "." + KEY + " ORDER BY " + table + "." + YEAR + " DESC";
        Cursor cursor = mDb.rawQuery(rawQuery, new String[]{query});

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
}
