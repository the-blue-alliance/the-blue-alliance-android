package com.thebluealliance.androidclient.datafeed;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.SimpleTeam;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;


/**
 * File created by phil on 4/28/14.
 */
public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "the-blue-alliance-android-database",
            TABLE_API = "api",
            TABLE_TEAMS = "teams";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO implement some upgrade code
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

    public long storeTeam(SimpleTeam team) {
        ContentValues cv = new ContentValues();
        cv.put(Teams.KEY, team.getTeamKey());
        cv.put(Teams.NUMBER, team.getTeamNumber());
        cv.put(Teams.NAME, team.getFullName());
        cv.put(Teams.SHORTNAME, team.getNickname());
        cv.put(Teams.LOCATION, team.getLocation());
        return db.insert(TABLE_TEAMS, null, cv);
    }

    public void storeTeams(ArrayList<SimpleTeam> teams) {
        db.beginTransaction();
        for (Team team : teams) {
            ContentValues cv = new ContentValues();
            cv.put(Teams.KEY, team.getTeamKey());
            cv.put(Teams.NUMBER, team.getTeamNumber());
            cv.put(Teams.NAME, team.getFullName());
            cv.put(Teams.SHORTNAME, team.getNickname());
            cv.put(Teams.LOCATION, team.getLocation());
            db.insert(TABLE_TEAMS, null, cv);
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

    public boolean exists(String url) {
        Cursor cursor = db.query(TABLE_API, new String[]{Response.URL}, Response.URL + "=?", new String[]{url}, null, null, null, null);
        return (cursor.moveToFirst()) || (cursor.getCount() != 0);
    }

    public int updateResponse(String url, String response, long updated) {
        ContentValues cv = new ContentValues();
        cv.put(Response.RESPONSE, response);
        cv.put(Response.LASTUPDATE, updated);
        return db.update(TABLE_API, cv, Response.URL + "=?", new String[]{url});
    }
}
