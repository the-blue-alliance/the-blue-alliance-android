package com.thebluealliance.androidclient.datafeed;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.LaunchActivity;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.SimpleEvent;
import com.thebluealliance.androidclient.models.SimpleTeam;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


/**
 * File created by phil on 4/28/14.
 */
public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 9;
    private Context context;
    public static final String DATABASE_NAME = "the-blue-alliance-android-database",
            TABLE_API = "api",
            TABLE_TEAMS = "teams",
            TABLE_EVENTS = "events",
            TABLE_SEARCH = "search",
            TABLE_SEARCH_TEAMS = "search_teams",
            TABLE_SEARCH_EVENTS = "search_events";

    String CREATE_API = "CREATE TABLE IF NOT EXISTS " + TABLE_API + "("
            + Response.URL + " TEXT PRIMARY KEY, "
            + Response.RESPONSE + " TEXT, "
            + Response.LASTUPDATE + " TIMESTAMP, "
            + Response.LASTHIT + " TIMESTAMP "
            + ")";
    String CREATE_TEAMS = "CREATE TABLE IF NOT EXISTS " + TABLE_TEAMS + "("
            + Teams.KEY + " TEXT PRIMARY KEY, "
            + Teams.NUMBER + " INTEGER NOT NULL, "
            + Teams.NAME + " TEXT, "
            + Teams.SHORTNAME + " TEXT, "
            + Teams.LOCATION + " TEXT"
            + ")";
    String CREATE_EVENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENTS + "("
            + Events.KEY + " TEXT PRIMARY KEY, "
            + Events.NAME + " TEXT, "
            + Events.LOCATION + " TEXT, "
            + Events.VENUE + " TEXT, "
            + Events.TYPE + " INTEGER, "
            + Events.DISTRICT + " INTEGER, "
            + Events.DISTRICT_STRING + " TEXT, "
            + Events.START + " TIMESTAMP, "
            + Events.END + " TIMESTAMP, "
            + Events.OFFICIAL + " INTEGER, "
            + Events.WEEK + " INTEGER"
            + ")";
    String CREATE_SEARCH_TEAMS = "CREATE VIRTUAL TABLE IF NOT EXISTS " + TABLE_SEARCH_TEAMS +
            " USING fts3 (" +
            SearchTeam.KEY + "," +
            SearchTeam.TITLES + "," +
            SearchTeam.NUMBER + ")";

    String CREATE_SEARCH_EVENTS = "CREATE VIRTUAL TABLE IF NOT EXISTS " + TABLE_SEARCH_EVENTS +
            " USING fts3 (" +
            SearchEvent.KEY + "," +
            SearchEvent.TITLES + "," +
            SearchEvent.YEAR + ")";

    protected SQLiteDatabase db;
    private static Database sDatabaseInstance;

    private Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        db = getWritableDatabase();
    }

    /**
     * USE THIS METHOD TO GAIN DATABASE REFERENCES!!11!!!
     * This makes sure that db accesses stay thread-safe
     * (which becomes important with multiple AsyncTasks working simultaneously).
     * Should work, per http://touchlabblog.tumblr.com/post/24474750219/single-sqlite-connection
     *
     * @param context Context used to create Database object, if necessary
     * @return Your synchronized reference to use.
     */
    public static synchronized Database getInstance(Context context) {
        if (sDatabaseInstance == null) {
            sDatabaseInstance = new Database(context.getApplicationContext());
        }
        return sDatabaseInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_API);
        db.execSQL(CREATE_TEAMS);
        db.execSQL(CREATE_EVENTS);
        db.execSQL(CREATE_SEARCH_TEAMS);
        db.execSQL(CREATE_SEARCH_EVENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(Constants.LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        if (newVersion >= DATABASE_VERSION && oldVersion < DATABASE_VERSION){
            /* For now, we're just gonna drop all data tables and wipe the data-related
               shared preferences just to put everyone on track starting from v8.

               For future versions, a possible idea is adding any previous db changes
               through a while loop/switch statement, incrementing up until the latest version.
               like http://blog.adamsbros.org/2012/02/28/upgrade-android-sqlite-database/

               If version is less than 8, then wipe remaining tables/data prefs.
            */
            // d-d-d-d-drop the tables (ノಠ益ಠ)ノ彡┻━┻ #dubstepwubwubz
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_API);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_TEAMS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_EVENTS);

            // Clear the data-related shared prefs
            Map<String, ?> allEntries = PreferenceManager.getDefaultSharedPreferences(context).getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                if (entry.getKey().toString().contains(com.thebluealliance.androidclient.datafeed.datamanger.Events.ALL_EVENTS_LOADED_TO_DATABASE_FOR_YEAR) ||
                    entry.getKey().toString().contains(com.thebluealliance.androidclient.datafeed.datamanger.Teams.ALL_TEAMS_LOADED_TO_DATABASE_FOR_PAGE))
                {
                    PreferenceManager.getDefaultSharedPreferences(context).edit().
                            remove(entry.getKey().toString()).commit();
                }
            }

            PreferenceManager.getDefaultSharedPreferences(context).edit().
                    remove(LaunchActivity.ALL_DATA_LOADED).commit();
        }

        onCreate(db);
    }

    public class Response {
        public static final String URL = "url", //text
                RESPONSE = "response",          //text
                LASTUPDATE = "lastUpdated",     //timestamp for Last-Modified from API
                LASTHIT = "lastHit";           //last time we hit the API
    }

    public class Teams {
        public static final String KEY = "key",
                NUMBER = "number",
                NAME = "name",
                SHORTNAME = "shortname",
                LOCATION = "location";
    }

    public class Events {
        public static final String KEY = "key",
                NAME = "name",
                LOCATION = "location",
                VENUE = "venue",
                TYPE = "eventType",
                DISTRICT = "eventDistrict",
                DISTRICT_STRING = "districtString",
                START = "startDate",
                END = "endDate",
                OFFICIAL = "official",
                WEEK = "competitionWeek";
    }

    public class SearchTeam {
        public static final String
                KEY = "key",
                TITLES = "titles",
                NUMBER = "number";
    }

    public class SearchEvent {
        public static final String
                KEY = "key",
                TITLES = "titles",
                YEAR = "year";
    }

    public long storeTeam(SimpleTeam team) {
        insertSearchItemTeam(team);
        return db.insert(TABLE_TEAMS, null, team.getParams());
    }

    public void storeTeams(ArrayList<SimpleTeam> teams) {
        db.beginTransaction();
        for (SimpleTeam team : teams) {
            storeTeam(team);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public SimpleTeam getTeam(String teamKey) {
        Cursor cursor = db.query(TABLE_TEAMS, new String[]{Teams.KEY, Teams.NUMBER, Teams.NAME, Teams.SHORTNAME, Teams.LOCATION},
                Teams.KEY + " = ?", new String[]{teamKey}, null, null, null, null
        );
        if (cursor != null && cursor.moveToFirst()) {
            SimpleTeam team = new SimpleTeam();
            team.setTeamKey(cursor.getString(0));
            team.setTeamNumber(cursor.getInt(1));
            team.setFullName(cursor.getString(2));
            team.setNickname(cursor.getString(3));
            team.setLocation(cursor.getString(4));
            cursor.close();
            return team;
        } else {
            return null;
        }
    }

    public ArrayList<SimpleTeam> getTeamsInRange(int lowerBound, int upperBound) {
        ArrayList<SimpleTeam> teams = new ArrayList<>();
        // ?+0 ensures that string arguments that are really numbers are cast to numbers for the query
        Cursor cursor = db.query(TABLE_TEAMS, new String[]{Teams.KEY, Teams.NUMBER, Teams.NAME, Teams.SHORTNAME, Teams.LOCATION},
                Teams.NUMBER + " BETWEEN ?+0 AND ?+0", new String[]{String.valueOf(lowerBound), String.valueOf(upperBound)}, null, null, null, null);
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            teams.add(new SimpleTeam(cursor.getString(0), cursor.getInt(1), cursor.getString(3), cursor.getString(4), -1));
        }
        cursor.close();
        return teams;
    }

    public Cursor getCursorForTeamsInRange(int lowerBound, int upperBound) {
        Cursor cursor = db.rawQuery("SELECT " + TABLE_TEAMS + ".rowid as '_id',"
                + Teams.KEY + ","
                + Teams.NUMBER + ","
                + Teams.NAME + ","
                + Teams.SHORTNAME + ","
                + Teams.LOCATION
                + " FROM " + TABLE_TEAMS + " WHERE " + Teams.NUMBER + " BETWEEN ?+0 AND ?+0 ORDER BY ? ASC", new String[]{String.valueOf(lowerBound), String.valueOf(upperBound), Teams.NUMBER});

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public long storeEvent(SimpleEvent event) {
        if (!eventExists(event.getEventKey())) {
            insertSearchItemEvent(event);
            return db.insert(TABLE_EVENTS, null, event.getParams());
        } else {
            return 0;//updateEvent(event);
        }
    }

    public void storeEvents(ArrayList<SimpleEvent> events) {
        db.beginTransaction();
        for (SimpleEvent event : events) {
            storeEvent(event);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public SimpleEvent getEvent(String eventKey) {
        Cursor cursor = db.query(TABLE_EVENTS, new String[]{Events.KEY, Events.NAME, Events.TYPE, Events.DISTRICT, Events.START,
                        Events.END, Events.LOCATION, Events.VENUE, Events.OFFICIAL, Events.DISTRICT_STRING},
                Events.KEY + " = ?", new String[]{eventKey}, null, null, null, null
        );
        if (cursor != null && cursor.moveToFirst()) {
            SimpleEvent event = new SimpleEvent();
            event.setEventKey(cursor.getString(0));
            event.setEventName(cursor.getString(1));
            event.setEventType(EventHelper.TYPE.values()[cursor.getInt(2)]);
            event.setDistrictEnum(cursor.getInt(3));
            event.setStartDate(cursor.getString(4));
            event.setEndDate(cursor.getString(5));
            event.setLocation(cursor.getString(6));
            event.setVenue(cursor.getString(7));
            event.setOfficial(cursor.getInt(8) == 1);
            event.setDistrictTitle(cursor.getString(9));
            cursor.close();
            return event;
        } else {
            return null;
        }
    }

    public ArrayList<SimpleEvent> getEventsInWeek(int year, int week) {
        ArrayList<SimpleEvent> events = new ArrayList<>();
        Cursor cursor = db.query(TABLE_EVENTS, new String[]{Events.KEY, Events.NAME, Events.TYPE, Events.DISTRICT, Events.START,
                        Events.END, Events.LOCATION, Events.VENUE, Events.OFFICIAL, Events.DISTRICT_STRING},
                Events.KEY + " LIKE ? AND " + Events.WEEK + " = ?", new String[]{Integer.toString(year) + "%", Integer.toString(week)}, null, null, null, null
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                SimpleEvent event = new SimpleEvent();
                event.setEventKey(cursor.getString(0));
                event.setEventName(cursor.getString(1));
                event.setEventType(EventHelper.TYPE.values()[cursor.getInt(2)]);
                event.setDistrictEnum(cursor.getInt(3));
                event.setStartDate(cursor.getString(4));
                event.setEndDate(cursor.getString(5));
                event.setLocation(cursor.getString(6));
                event.setVenue(cursor.getString(7));
                event.setOfficial(cursor.getInt(8) == 1);
                event.setDistrictTitle(cursor.getString(9));
                events.add(event);
            } while (cursor.moveToNext());
            cursor.close();
            return events;
        } else {
            Log.w(Constants.LOG_TAG, "Failed to find events in " + year + " week " + week);
            return null;
        }
    }

    public ArrayList<SimpleEvent> getEventsInYear(int year) {
        ArrayList<SimpleEvent> events = new ArrayList<>();
        Cursor cursor = db.query(TABLE_EVENTS, new String[]{Events.KEY, Events.NAME, Events.TYPE, Events.DISTRICT, Events.START,
                        Events.END, Events.LOCATION, Events.VENUE, Events.OFFICIAL, Events.DISTRICT_STRING},
                Events.KEY + " LIKE ?", new String[]{Integer.toString(year) + "%"}, null, null, null, null
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                SimpleEvent event = new SimpleEvent();
                event.setEventKey(cursor.getString(0));
                event.setEventName(cursor.getString(1));
                event.setEventType(EventHelper.TYPE.values()[cursor.getInt(2)]);
                event.setDistrictEnum(cursor.getInt(3));
                event.setStartDate(cursor.getString(4));
                event.setEndDate(cursor.getString(5));
                event.setLocation(cursor.getString(6));
                event.setVenue(cursor.getString(7));
                event.setOfficial(cursor.getInt(8) == 1);
                event.setDistrictTitle(cursor.getString(9));
                events.add(event);
            } while (cursor.moveToNext());
            cursor.close();
            return events;
        } else {
            Log.w(Constants.LOG_TAG, "Failed to find events in " + year);
            return null;
        }
    }

    public boolean eventExists(String key) {
        Cursor cursor = db.query(TABLE_EVENTS, new String[]{Events.KEY}, Events.KEY + "=?", new String[]{key}, null, null, null, null);
        boolean result;
        if (cursor != null) {
            result = cursor.moveToFirst();
            cursor.close();
        } else {
            result = false;
        }
        return result;
    }

    public int updateEvent(SimpleEvent event) {
        updateSearchItemEvent(event);
        return db.update(TABLE_EVENTS, event.getParams(), Events.KEY + "=?", new String[]{event.getEventKey()});
    }

    public APIResponse<String> getResponse(String url) {
        Cursor cursor = db.query(TABLE_API, new String[]{Response.URL, Response.RESPONSE, Response.LASTUPDATE, Response.LASTHIT},
                Response.URL + "=?", new String[]{url}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String response = cursor.getString(1),
                    lastUpdate = cursor.getString(2);
            long lastHit = cursor.getLong(3);
            cursor.close();
            return new APIResponse<>(response, APIResponse.CODE.LOCAL, lastUpdate, new Date(lastHit));
        } else {
            Log.w(Constants.LOG_TAG, "Failed to find response in database with url " + url);
            return null;
        }
    }

    public long storeResponse(String url, String response, String updated) {
        ContentValues cv = new ContentValues();
        cv.put(Response.URL, url);
        cv.put(Response.RESPONSE, response);
        cv.put(Response.LASTUPDATE, updated);
        cv.put(Response.LASTHIT, new Date().getTime());
        return db.insert(TABLE_API, null, cv);
    }

    public boolean responseExists(String url) {
        Cursor cursor = db.query(TABLE_API, new String[]{Response.URL}, Response.URL + "=?", new String[]{url}, null, null, null, null);
        boolean exists = (cursor.moveToFirst()) || (cursor.getCount() != 0);
        cursor.close();
        return exists;
    }

    public int deleteResponse(String url) {
        if (responseExists(url)) {
            return db.delete(TABLE_API, Response.URL + "=?", new String[]{url});
        }
        return 0;
    }

    public void deleteAllResponses() {
        getWritableDatabase().execSQL("delete from " + TABLE_API);
    }

    public int updateResponse(String url, String response, String updated) {
        ContentValues cv = new ContentValues();
        cv.put(Response.RESPONSE, response);
        cv.put(Response.LASTUPDATE, updated);
        cv.put(Response.LASTHIT, new Date().getTime());
        return db.update(TABLE_API, cv, Response.URL + "=?", new String[]{url});
    }

    public long insertSearchItemTeam(Team team) {
        ContentValues cv = new ContentValues();
        cv.put(SearchTeam.KEY, team.getTeamKey());
        cv.put(SearchTeam.TITLES, Utilities.getAsciiApproximationOfUnicode(team.getSearchTitles()));
        cv.put(SearchTeam.NUMBER, team.getTeamNumber());
        return db.insert(TABLE_SEARCH_TEAMS, null, cv);
    }

    public long insertSearchItemEvent(Event event) {
        ContentValues cv = new ContentValues();
        cv.put(SearchEvent.KEY, event.getEventKey());
        cv.put(SearchEvent.TITLES, Utilities.getAsciiApproximationOfUnicode(event.getSearchTitles()));
        cv.put(SearchEvent.YEAR, event.getEventYear());
        return db.insert(TABLE_SEARCH_EVENTS, null, cv);
    }

    public long updateSearchItemTeam(Team team) {
        ContentValues cv = new ContentValues();
        cv.put(SearchTeam.KEY, team.getTeamKey());
        cv.put(SearchTeam.TITLES, Utilities.getAsciiApproximationOfUnicode(team.getSearchTitles()));
        cv.put(SearchTeam.NUMBER, team.getTeamNumber());
        return db.update(TABLE_SEARCH_TEAMS, cv, SearchTeam.KEY + "=?", new String[]{team.getTeamKey()});
    }

    public long updateSearchItemEvent(Event event) {
        ContentValues cv = new ContentValues();
        cv.put(SearchEvent.KEY, event.getEventKey());
        cv.put(SearchEvent.TITLES, Utilities.getAsciiApproximationOfUnicode(event.getSearchTitles()));
        cv.put(SearchEvent.YEAR, event.getEventYear());
        return db.update(TABLE_SEARCH_EVENTS, cv, SearchEvent.KEY + "=?", new String[]{event.getEventKey()});
    }

    public Cursor getMatchesForTeamQuery(String query) {
        String selection = SearchTeam.TITLES + " MATCH ?";
        String[] selectionArgs = new String[]{query};

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE_SEARCH_TEAMS);

        Cursor cursor = builder.query(this.getReadableDatabase(),
                new String[]{SearchTeam.KEY, SearchTeam.TITLES, SearchTeam.NUMBER}, selection, selectionArgs, null, null, SearchTeam.NUMBER + " ASC");

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public Cursor getMatchesForEventQuery(String query) {
        String selection = SearchEvent.TITLES + " MATCH ?";
        String[] selectionArgs = new String[]{query};

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE_SEARCH_EVENTS);

        Cursor cursor = builder.query(this.getReadableDatabase(),
                new String[]{SearchEvent.KEY, SearchEvent.TITLES, SearchEvent.YEAR}, selection, selectionArgs, null, null, SearchEvent.YEAR + " DESC");

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public Cursor getTeamsForTeamQuery(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS tempteams");
        String createTempTeams = "CREATE TEMP TABLE tempteams (tempkey TEXT)";
        db.execSQL(createTempTeams);
        db.execSQL("INSERT INTO tempteams SELECT " + SearchTeam.KEY + " FROM " + TABLE_SEARCH_TEAMS + " WHERE " + SearchTeam.TITLES + " MATCH ?", new String[]{query});
        Cursor cursor = db.rawQuery("SELECT " + TABLE_TEAMS + ".rowid as '_id',"
                + Teams.KEY + ","
                + Teams.NUMBER + ","
                + Teams.NAME + ","
                + Teams.SHORTNAME + ","
                + Teams.LOCATION
                + " FROM " + TABLE_TEAMS + " JOIN tempteams ON tempteams.tempkey = " + TABLE_TEAMS + "." + Teams.KEY + " ORDER BY ? ASC", new String[]{Teams.NUMBER});

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public Cursor getEventsForQuery(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS tempevents");
        String createTempTeams = "CREATE TEMP TABLE tempevents (tempkey TEXT)";
        db.execSQL(createTempTeams);
        db.execSQL("INSERT INTO tempevents SELECT " + SearchTeam.KEY + " FROM " + TABLE_SEARCH_EVENTS + " WHERE " + SearchEvent.TITLES + " MATCH ?", new String[]{query});
        Cursor cursor = db.rawQuery("SELECT " + TABLE_EVENTS + ".rowid as '_id',"
                        + Events.KEY + ","
                        + Events.NAME + ","
                        + Events.TYPE + ","
                        + Events.DISTRICT + ","
                        + Events.START + ","
                        + Events.END + ","
                        + Events.LOCATION + ","
                        + Events.VENUE + ","
                        + Events.OFFICIAL + ","
                        + Events.DISTRICT_STRING
                        + " FROM " + TABLE_EVENTS + " JOIN tempevents ON tempevents.tempkey = " + TABLE_EVENTS + "." + Events.KEY + " ORDER BY ? DESC", new String[]{SearchEvent.YEAR}
        );

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
}
