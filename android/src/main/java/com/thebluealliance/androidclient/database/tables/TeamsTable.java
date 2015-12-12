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
import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TeamsTable extends ModelTable<Team> {
    public static final String KEY = "key",
            NUMBER = "number",
            NAME = "name",
            SHORTNAME = "shortname",
            LOCATION = "location",
            WEBSITE = "website",
            YEARS_PARTICIPATED = "yearsParticipated",
            MOTTO = "motto";

    private SQLiteDatabase mDb;

    @Inject
    public TeamsTable(SQLiteDatabase db) {
        super(db);
        mDb = db;
    }

    @Override
    protected void insertCallback(Team team) {
        ContentValues cv = new ContentValues();
        try {
            cv.put(Database.SearchTeam.KEY, team.getKey());
            cv.put(Database.SearchTeam.TITLES, Utilities.getAsciiApproximationOfUnicode(team.getSearchTitles()));
            cv.put(Database.SearchTeam.NUMBER, team.getTeamNumber());
            mDb.insert(Database.TABLE_SEARCH_TEAMS, null, cv);
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Can't insert search team without the following fields:" +
                    "Database.Teams.KEY, Database.Teams.NUMBER");
        } catch (SQLiteException e) {
            Log.w(Constants.LOG_TAG, "Trying to add a SearchTeam that already exists. " + team.getKey());
        }
    }

    @Override
    protected void updateCallback(Team team) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(Database.SearchTeam.KEY, team.getKey());
            cv.put(Database.SearchTeam.TITLES, Utilities.getAsciiApproximationOfUnicode(team.getSearchTitles()));
            cv.put(Database.SearchTeam.NUMBER, team.getTeamNumber());
            mDb.update(Database.TABLE_SEARCH_TEAMS, cv, Database.SearchTeam.KEY + "=?", new String[]{team.getKey()});
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.e(Constants.LOG_TAG, "Can't insert event search item without the following fields:" +
                    "Database.Events.KEY, Database.Events.YEAR");
        }
    }

    @Override
    protected void deleteCallback(Team team) {
        mDb.delete(Database.TABLE_SEARCH_TEAMS, Database.SearchTeam.KEY + " = ?", new String[]{team.getKey()});
    }

    @Override
    protected void deleteAllCallback() {
        mDb.execSQL("delete from " + Database.TABLE_SEARCH_TEAMS);
    }

    public String getTableName() {
        return Database.TABLE_TEAMS;
    }

    @Override
    public String getKeyColumn() {
        return KEY;
    }

    @Override
    public Team inflate(Cursor cursor) {
        return ModelInflater.inflateTeam(cursor);
    }

    public Cursor getCursorForTeamsInRange(int lowerBound, int upperBound) {
        Cursor cursor = mDb.rawQuery("SELECT " + Database.TABLE_TEAMS + ".rowid as '_id',"
                + TeamsTable.KEY + ","
                + TeamsTable.NUMBER + ","
                + TeamsTable.NAME + ","
                + TeamsTable.SHORTNAME + ","
                + TeamsTable.LOCATION
                + " FROM " + Database.TABLE_TEAMS + " WHERE " + TeamsTable.NUMBER + " BETWEEN ?+0 AND ?+0 ORDER BY ? ASC", new String[]{String.valueOf(lowerBound), String.valueOf(upperBound), TeamsTable.NUMBER});

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public void deleteAllSearchIndexes() {
        mDb.rawQuery("DELETE FROM " + getTableName(), new String[]{});
    }

    public void deleteSearchIndex(Team team) {
        deleteCallback(team);
    }

    public void recreateAllSearchIndexes(List<Team> teams) {
        mDb.beginTransaction();
        try {
            for (int i = 0; i < teams.size(); i++) {
                insertCallback(teams.get(i));
            }
        } finally {
            mDb.setTransactionSuccessful();
        }
        mDb.endTransaction();
    }

    /**
     * Used in {@link com.thebluealliance.androidclient.activities.MoreSearchResultsActivity}
     * If you change the ordering of the rows selected, be sure that you also updated indexes in
     * {@link com.thebluealliance.androidclient.adapters.TeamCursorAdapter}
     */
    public Cursor getForSearchQuery(String query) {
        String table = getTableName();
        String searchTable = Database.TABLE_SEARCH_TEAMS;
        String rawQuery = "SELECT " + table + ".rowid as '_id',"
                + table + "." + KEY + ","
                + table + "." + NUMBER + ","
                + table + "." + NAME + ","
                + table + "." + SHORTNAME + ","
                + table + "." + LOCATION
                + " FROM " + table
                + " JOIN  (SELECT " + searchTable + "." + Database.SearchTeam.KEY + " FROM " + searchTable + " WHERE " + Database.SearchTeam.TITLES + " MATCH ?)"
                + " as 'tempteams' ON tempteams." + Database.SearchEvent.KEY + " = " + table + "." + KEY + " ORDER BY " + NUMBER + " ASC";
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
