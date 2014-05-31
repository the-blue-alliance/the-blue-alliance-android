package com.thebluealliance.androidclient.datafeed;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.SimpleEvent;
import com.thebluealliance.androidclient.models.SimpleTeam;

import java.util.ArrayList;


/**
 * File created by phil on 4/28/14.
 */
public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "the-blue-alliance-android-database",
            TABLE_API = "api",
            TABLE_TEAMS = "teams",
            TABLE_EVENTS = "events";

    protected SQLiteDatabase db;
    private static Database sDatabaseInstance;

    private Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
            sDatabaseInstance = new Database(context);
        }
        return sDatabaseInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_API = "CREATE TABLE " + TABLE_API + "("
                + Response.URL + " TEXT PRIMARY KEY, "
                + Response.RESPONSE + " TEXT, "
                + Response.LASTUPDATE + " TIMESTAMP "
                + ")";
        db.execSQL(CREATE_API);

        String CREATE_TEAMS = "CREATE TABLE " + TABLE_TEAMS + "("
                + Teams.KEY + " TEXT PRIMARY KEY, "
                + Teams.NUMBER + " INTEGER NOT NULL, "
                + Teams.NAME + " TEXT, "
                + Teams.SHORTNAME + " TEXT, "
                + Teams.LOCATION + " TEXT"
                + ")";
        db.execSQL(CREATE_TEAMS);

        String CREATE_EVENTS = "CREATE TABLE " + TABLE_EVENTS + "("
                + Events.KEY + " TEXT PRIMARY KEY, "
                + Events.NAME + " TEXT, "
                + Events.LOCATION + " TEXT, "
                + Events.TYPE + " INTEGER, "
                + Events.DISTRICT + " INTEGER, "
                + Events.START + " TIMESTAMP, "
                + Events.END + " TIMESTAMP, "
                + Events.OFFICIAL + " INTEGER, "
                + Events.WEEK + " INTEGER"
                + ")";
        db.execSQL(CREATE_EVENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public class Response {
        public static final String URL = "url",       //text
                RESPONSE = "response",      //text
                LASTUPDATE = "lastUpdated";    //timestamp

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
                TYPE = "eventType",
                DISTRICT = "eventDistrict",
                START = "startDate",
                END = "endDate",
                OFFICIAL = "official",
                WEEK = "competitionWeek";
    }

    public long storeTeam(SimpleTeam team) {
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

    public ArrayList<SimpleTeam> getTeamsInRange(int lowerBound, int upperBound) {
        ArrayList<SimpleTeam> teams = new ArrayList<>();
        // ?+0 ensures that string arguments that are really numbers are cast to numbers for the query
        Cursor cursor = db.query(TABLE_TEAMS, new String[]{Teams.KEY, Teams.NUMBER, Teams.NAME, Teams.SHORTNAME, Teams.LOCATION},
                Teams.NUMBER + " BETWEEN ?+0 AND ?+0", new String[]{"" + lowerBound, "" + upperBound}, null, null, null, null);
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            teams.add(new SimpleTeam(cursor.getString(0), cursor.getInt(1), cursor.getString(3), cursor.getString(4), -1));
        }
        return teams;
    }

    public long storeEvent(SimpleEvent event) {
        if (!eventExists(event.getEventKey())) {
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
                        Events.END, Events.LOCATION, Events.OFFICIAL},
                Events.KEY + " = ?", new String[]{eventKey}, null, null, null, null
        );
        if(cursor != null && cursor.moveToFirst()) {
            SimpleEvent event = new SimpleEvent();
            event.setEventKey(cursor.getString(0));
            event.setEventName(cursor.getString(1));
            event.setEventType(Event.TYPE.values()[cursor.getInt(2)]);
            event.setEventDistrict(Event.DISTRICT.values()[cursor.getInt(3)]);
            event.setStartDate(cursor.getString(4));
            event.setEndDate(cursor.getString(5));
            event.setLocation(cursor.getString(6));
            event.setOfficial(cursor.getInt(7) == 1);
            return event;
        } else {
            return null;
        }
    }

    public ArrayList<SimpleEvent> getEventsInWeek(int year, int week) {
        ArrayList<SimpleEvent> events = new ArrayList<>();
        Cursor cursor = db.query(TABLE_EVENTS, new String[]{Events.KEY, Events.NAME, Events.TYPE, Events.DISTRICT, Events.START,
                        Events.END, Events.LOCATION, Events.OFFICIAL},
                Events.KEY + " LIKE ? AND " + Events.WEEK + " = ?", new String[]{Integer.toString(year) + "%", Integer.toString(week)}, null, null, null, null
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                SimpleEvent event = new SimpleEvent();
                event.setEventKey(cursor.getString(0));
                event.setEventName(cursor.getString(1));
                event.setEventType(Event.TYPE.values()[cursor.getInt(2)]);
                event.setEventDistrict(Event.DISTRICT.values()[cursor.getInt(3)]);
                event.setStartDate(cursor.getString(4));
                event.setEndDate(cursor.getString(5));
                event.setLocation(cursor.getString(6));
                event.setOfficial(cursor.getInt(7) == 1);

                events.add(event);
            } while (cursor.moveToNext());
            return events;
        } else {
            Log.w(Constants.LOG_TAG, "Failed to find events in " + year + " week " + week);
            return null;
        }
    }

    public ArrayList<SimpleEvent> getEventsInYear(int year) {
        ArrayList<SimpleEvent> events = new ArrayList<>();
        Cursor cursor = db.query(TABLE_EVENTS, new String[]{Events.KEY, Events.NAME, Events.TYPE, Events.DISTRICT, Events.START,
                        Events.END, Events.LOCATION, Events.OFFICIAL},
                Events.KEY + " LIKE ?", new String[]{Integer.toString(year) + "%"}, null, null, null, null
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                SimpleEvent event = new SimpleEvent();
                event.setEventKey(cursor.getString(0));
                event.setEventName(cursor.getString(1));
                event.setEventType(Event.TYPE.values()[cursor.getInt(2)]);
                event.setEventDistrict(Event.DISTRICT.values()[cursor.getInt(3)]);
                event.setStartDate(cursor.getString(4));
                event.setEndDate(cursor.getString(5));
                event.setLocation(cursor.getString(6));
                event.setOfficial(cursor.getInt(7) == 1);

                events.add(event);
            } while (cursor.moveToNext());
            return events;
        } else {
            Log.w(Constants.LOG_TAG, "Failed to find events in " + year);
            return null;
        }
    }

    public boolean eventExists(String key) {
        Cursor cursor = db.query(TABLE_EVENTS, new String[]{Events.KEY}, Events.KEY + "=?", new String[]{key}, null, null, null, null);
        return cursor != null && cursor.moveToFirst();
    }

    public int updateEvent(SimpleEvent in) {
        return db.update(TABLE_EVENTS, in.getParams(), Events.KEY + "=?", new String[]{in.getEventKey()});
    }

    public String getResponse(String url) {
        Cursor cursor = db.query(TABLE_API, new String[]{Response.URL, Response.RESPONSE, Response.LASTUPDATE},
                Response.URL + "=?", new String[]{url}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor.getString(1);
        } else {
            Log.w(Constants.LOG_TAG, "Failed to find response in database with url " + url);
            return null;
        }
    }

    public long storeResponse(String url, String response, long updated) {
        ContentValues cv = new ContentValues();
        cv.put(Response.URL, url);
        cv.put(Response.RESPONSE, response);
        cv.put(Response.LASTUPDATE, updated);
        return db.insert(TABLE_API, null, cv);
    }

    public boolean responseExists(String url) {
        Cursor cursor = db.query(TABLE_API, new String[]{Response.URL}, Response.URL + "=?", new String[]{url}, null, null, null, null);
        return (cursor.moveToFirst()) || (cursor.getCount() != 0);
    }

    public int deleteResponse(String url) {
        if(responseExists(url)){
            return db.delete(TABLE_API, Response.URL + "=?", new String[]{url});
        }
        return 0;
    }

    public void deleteAllResponses() {
        getWritableDatabase().execSQL("delete from " + TABLE_API);
    }

    public int updateResponse(String url, String response, long updated) {
        ContentValues cv = new ContentValues();
        cv.put(Response.RESPONSE, response);
        cv.put(Response.LASTUPDATE, updated);
        return db.update(TABLE_API, cv, Response.URL + "=?", new String[]{url});
    }
}
