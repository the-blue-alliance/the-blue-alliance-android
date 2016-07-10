package com.thebluealliance.androidclient.database.writers;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelTable;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import rx.functions.Action4;
import rx.schedulers.Schedulers;

/**
 * Common code for a Database Writer
 * @param <T> Type of object to be written (e.g. model type of list of models)
 */
public abstract class BaseDbWriter<T> implements Action4<String, String, String[], T> {

    protected final Database mDb;
    protected final BriteDatabase mBriteDb;

    public BaseDbWriter(Database db, BriteDatabase briteDb) {
        mDb = db;
        mBriteDb = briteDb;
    }

    /**
     * Writes new models to the db
     * @param newModels New models to write
     */
    @WorkerThread
    public abstract void write(T newModels);

    /**
     * Delete the objects associated with the query in the db
     * So we can account for deletions
     * @param dbTable String TABLE_* constant from {@link Database}
     * @param sqlWhere SQL WHERE Clause
     * @param whereArgs args for WHERE clause
     */
    @WorkerThread
    public void clear(
      @Nullable String dbTable,
      @Nullable String sqlWhere,
      @Nullable String[] whereArgs) {
        if (dbTable == null || sqlWhere == null || whereArgs == null) {
            // No clearing to do
            return;
        }
        ModelTable table = getTable(mDb, dbTable);
        if (table != null) {
            table.delete(sqlWhere, whereArgs);
        }
    }

    @Override
    public void call(
      @Nullable String dbTable,
      @Nullable String sqlWhere,
      @Nullable String[] whereArgs,
      T newModels) {
        if (newModels == null) {
            return;
        }
        Schedulers.io().createWorker().schedule(() -> {
            BriteDatabase.Transaction transaction = mBriteDb.newTransaction();
            try {
                clear(dbTable, sqlWhere, whereArgs);
                write(newModels);
                transaction.markSuccessful();
            } finally {
                transaction.end();
            }
        });
    }

    /**
     * Returns the ModelTable associated with the given string
     * @param tableName A TABLE_* constant from {@link Database}
     */
    private static @Nullable ModelTable getTable(Database db, String tableName) {
        switch (tableName) {
            case Database.TABLE_AWARDS:
                return db.getAwardsTable();
            case Database.TABLE_DISTRICTS:
                return db.getDistrictsTable();
            case Database.TABLE_DISTRICTTEAMS:
                return db.getDistrictTeamsTable();
            case Database.TABLE_EVENTS:
                return db.getEventsTable();
            case Database.TABLE_EVENTTEAMS:
                return db.getEventTeamsTable();
            case Database.TABLE_MATCHES:
                return db.getMatchesTable();
            case Database.TABLE_MEDIAS:
                return db.getMediasTable();
            case Database.TABLE_TEAMS:
                return db.getTeamsTable();
            default:
                return null;
        }
    }
}
