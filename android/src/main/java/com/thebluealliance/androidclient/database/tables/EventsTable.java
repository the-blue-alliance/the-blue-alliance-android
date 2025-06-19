package com.thebluealliance.androidclient.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.database.model.DistrictDbModel;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;
import java.util.List;

public class EventsTable extends ModelTable<Event> {
    public static final String KEY = "key",
            YEAR = "year",
            NAME = "name",
            SHORTNAME = "shortName",
            LOCATION = "location",
            CITY = "city",
            VENUE = "venue",
            ADDRESS = "venue_address",
            TYPE = "eventType",
            START = "startDate",
            END = "endDate",
            WEEK = "competitionWeek",
            WEBCASTS = "webcasts",
            WEBSITE = "website",
            DISTRICT_KEY = "district_key",
            LAST_MODIFIED = "last_modified";


    public static @Deprecated final String
            DISTRICT = "eventDistrict",
            DISTRICT_STRING = "districtString",
            DISTRICT_POINTS = "districtPoints",
            RANKINGS = "rankings",
            ALLIANCES = "alliances",
            OFFICIAL = "official",
            TEAMS = "teams",
            STATS = "stats";

    private DistrictsTable mDistrictsTable;

    public EventsTable(SQLiteDatabase db, Gson gson, DistrictsTable districtsTable) {
        super(db, gson);
        mDistrictsTable = districtsTable;
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
    static String getAllColumnsForJoin() {
        List<String> columns = new ArrayList<>();
        columns.add(KEY);
        columns.add(YEAR);
        columns.add(NAME);
        columns.add(SHORTNAME);
        columns.add(LOCATION);
        columns.add(VENUE);
        columns.add(TYPE);
        columns.add(START);
        columns.add(END);
        columns.add(WEEK);
        columns.add(WEBCASTS);
        columns.add(WEBSITE);
        columns.add(DISTRICT_KEY);

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
        Database.beginTransaction(mDb);
        try {
            ContentValues cv = new ContentValues();
            cv.put(Database.SearchEvent.KEY, event.getKey());
            cv.put(Database.SearchEvent.TITLES,
                   Utilities.getAsciiApproximationOfUnicode(event.getSearchTitles()));
            cv.put(Database.SearchEvent.YEAR, event.getYear());
            mDb.insert(Database.TABLE_SEARCH_EVENTS, null, cv);

            if (event.getDistrict() != null) {
                District district = (District) event.getDistrict();
                mDistrictsTable.add(DistrictDbModel.fromDistrict(district), event.getLastModified());
            }
            mDb.setTransactionSuccessful();
        } catch (Exception ex) {
            TbaLogger.w("Error in Event insert callback", ex);
        } finally {
            mDb.endTransaction();
        }
    }

    @Override
    protected void updateCallback(Event event) {
        Database.beginTransaction(mDb);
        try {
            ContentValues cv = new ContentValues();
            cv.put(Database.SearchEvent.KEY, event.getKey());
            cv.put(Database.SearchEvent.TITLES,
                   Utilities.getAsciiApproximationOfUnicode(event.getSearchTitles()));
            cv.put(Database.SearchEvent.YEAR, event.getYear());

            mDb.update(Database.TABLE_SEARCH_EVENTS,
                       cv,
                       Database.SearchEvent.KEY + "=?",
                       new String[]{event.getKey()});
            mDb.setTransactionSuccessful();
        } catch (Exception ex) {
            TbaLogger.w("Error in Event update callback", ex);
        } finally {
            mDb.endTransaction();
        }
    }

    @Override
    protected void deleteCallback(Event event) {
        Database.beginTransaction(mDb);
        try {
            mDb.delete(Database.TABLE_SEARCH_EVENTS,
                       Database.SearchEvent.KEY + " = ?",
                       new String[]{event.getKey()});
            mDb.setTransactionSuccessful();
        } catch (Exception ex) {
            TbaLogger.w("Error in Event delete callback", ex);
        } finally {
            mDb.endTransaction();
        }
    }

    @Override
    protected void deleteAllCallback() {
        Database.beginTransaction(mDb);
        try {
            mDb.execSQL("delete from " + Database.TABLE_SEARCH_EVENTS);
            mDb.setTransactionSuccessful();
        } catch (Exception ex) {
            TbaLogger.w("Error in Event delete all callback", ex);
        } finally {
            mDb.endTransaction();
        }
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
    public String getLastModifiedColumn() {
        return LAST_MODIFIED;
    }

    @Override
    public Event inflate(Cursor cursor) {
        Event event = ModelInflater.inflateEvent(cursor);
        if (event.getDistrictKey() != null) {
            DistrictDbModel dbDistrict = mDistrictsTable.get(event.getDistrictKey());
            if (dbDistrict != null) {
                event.setDistrict(dbDistrict.toDistrict());
            }
        }
        return event;
    }

    public void deleteAllSearchIndexes() {
        Database.beginTransaction(mDb);
        try {
            mDb.rawQuery("DELETE FROM " + getTableName(), new String[]{});
            mDb.setTransactionSuccessful();
        } catch (Exception ex) {
            TbaLogger.w("Error in delete all search indexes", ex);
        } finally {
            mDb.endTransaction();
        }
    }

    public void deleteSearchIndex(Event event) {
        deleteCallback(event);
    }

    public void recreateAllSearchIndexes(List<Event> events) {
        Database.beginTransaction(mDb);
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
                + table + "." + START + ","
                + table + "." + END + ","
                + table + "." + LOCATION + ","
                + table + "." + VENUE
                + " FROM " + getTableName()
                + " JOIN (SELECT " + searchTable + "." + Database.SearchEvent.KEY + " FROM " + searchTable + " WHERE " + searchTable + "." + Database.SearchEvent.TITLES + " MATCH ?)"
                + " as 'tempevents' ON tempevents." + Database.SearchEvent.KEY + " = " + table + "." + KEY + " ORDER BY " + table + "." + YEAR + " DESC";
        Cursor cursor = null;
        try {
            cursor = mDb.rawQuery(rawQuery, new String[]{query});
        } catch (Exception ex) {
            TbaLogger.w("Can't fetch events from search query", ex);
        }

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
}
