package com.thebluealliance.androidclient.database.tables;

import com.google.gson.Gson;

import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.models.Team;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;

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
            ADDRESS = "address",
            LOCATION_NAME = "location_name",
            WEBSITE = "website",
            YEARS_PARTICIPATED = "yearsParticipated",
            MOTTO = "motto",
            LAST_MODIFIED = "last_modified";

    @Inject
    public TeamsTable(SQLiteDatabase db, Gson gson) {
        super(db, gson);
    }

    @Override
    protected void insertCallback(Team team) {
        ContentValues cv = new ContentValues();
        mDb.beginTransaction();
        try {
            cv.put(Database.SearchTeam.KEY, team.getKey());
            cv.put(Database.SearchTeam.TITLES, Utilities.getAsciiApproximationOfUnicode(team.getSearchTitles()));
            cv.put(Database.SearchTeam.NUMBER, team.getTeamNumber());
            mDb.insert(Database.TABLE_SEARCH_TEAMS, null, cv);
            mDb.setTransactionSuccessful();
        } catch (SQLiteException ex) {
            if (ex instanceof SQLiteDatabaseLockedException) {
                TbaLogger.d("Databse locked: " + ex.getMessage());
            } else {
                TbaLogger.w("Error in team insert callback", ex);
            }
        } finally {
            mDb.endTransaction();
        }
    }

    @Override
    protected void updateCallback(Team team) {
        Database.beginTransaction(mDb);
        try {
            ContentValues cv = new ContentValues();
            cv.put(Database.SearchTeam.KEY, team.getKey());
            cv.put(Database.SearchTeam.TITLES,
                   Utilities.getAsciiApproximationOfUnicode(team.getSearchTitles()));
            cv.put(Database.SearchTeam.NUMBER, team.getTeamNumber());
            mDb.update(Database.TABLE_SEARCH_TEAMS,
                       cv,
                       Database.SearchTeam.KEY + "=?",
                       new String[]{team.getKey()});
            mDb.setTransactionSuccessful();
        } catch (Exception ex) {
            TbaLogger.w("Error in team update callback", ex);
        } finally {
            mDb.endTransaction();
        }
    }

    @Override
    protected void deleteCallback(Team team) {
        Database.beginTransaction(mDb);
        try {
            mDb.delete(Database.TABLE_SEARCH_TEAMS,
                       Database.SearchTeam.KEY + " = ?",
                       new String[]{team.getKey()});
            mDb.setTransactionSuccessful();
        } catch (Exception ex) {
            TbaLogger.w("Error in team delete callback", ex);
        } finally {
            mDb.endTransaction();
        }
    }

    @Override
    protected void deleteAllCallback() {
        Database.beginTransaction(mDb);
        try {
            mDb.execSQL("delete from " + Database.TABLE_SEARCH_TEAMS);
            mDb.setTransactionSuccessful();
        } catch (Exception ex) {
            TbaLogger.w("Error in team delete all callback", ex);
        } finally {
            mDb.endTransaction();
        }
    }

    public String getTableName() {
        return Database.TABLE_TEAMS;
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
    public Team inflate(Cursor cursor) {
        return ModelInflater.inflateTeam(cursor);
    }

    public void deleteAllSearchIndexes() {
        Database.beginTransaction(mDb);
        try {
            mDb.rawQuery("DELETE FROM " + getTableName(), new String[]{});
            mDb.setTransactionSuccessful();
        } catch (Exception ex) {
            TbaLogger.w("Error in recreate all team indexes", ex);
        } finally {
            mDb.endTransaction();
        }
    }

    public void deleteSearchIndex(Team team) {
        deleteCallback(team);
    }

    public void recreateAllSearchIndexes(List<Team> teams) {
        Database.beginTransaction(mDb);
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
        Cursor cursor = null;
        try {
            cursor = mDb.rawQuery(rawQuery, new String[]{query});
        } catch (Exception ex) {
            TbaLogger.w("Can't fetch teams from search query", ex);
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
