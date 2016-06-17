package com.thebluealliance.androidclient.database.tables;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.models.StoredNotification;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class NotificationsTable {
    public static final String
            ID = "_id",
            TYPE = "type",
            TITLE = "title",
            BODY = "body",
            INTENT = "intent",
            TIME = "time",
            SYSTEM_ID = "system_id",
            ACTIVE = "active",
            MSG_DATA = "msg_data";

    private SQLiteDatabase mDb;
    private BriteDatabase mBriteDb;

    public NotificationsTable(SQLiteDatabase db, BriteDatabase briteDb) {
        mDb = db;
        mBriteDb = briteDb;
    }

    public void add(StoredNotification... in) {
        BriteDatabase.Transaction transaction = mBriteDb.newTransaction();
        for (StoredNotification notification : in) {
            mBriteDb.insert(Database.TABLE_NOTIFICATIONS, notification.getParams());
        }
        transaction.markSuccessful();
        transaction.end();
    }

    public List<StoredNotification> get() {
        ArrayList<StoredNotification> out = new ArrayList<>();
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + Database.TABLE_NOTIFICATIONS + " ORDER BY " + ID + " DESC", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                out.add(ModelInflater.inflateStoredNotification(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return out;
    }

    public Observable<List<StoredNotification>> getObservable() {
        Observable<SqlBrite.Query> briteQuery = mBriteDb.createQuery(Database.TABLE_NOTIFICATIONS, "SELECT * FROM " + Database.TABLE_NOTIFICATIONS + " ORDER BY " + ID + " DESC");
        return briteQuery.map(query -> {
            Cursor cursor = query.run();
            List<StoredNotification> out = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    out.add(ModelInflater.inflateStoredNotification(cursor));
                } while (cursor.moveToNext());
                cursor.close();
            }
            return out;
        });
    }

    public List<StoredNotification> getActive() {
        ArrayList<StoredNotification> out = new ArrayList<>();
        Cursor cursor = mDb.query(Database.TABLE_NOTIFICATIONS, null, ACTIVE + " = 1", null, null, null, ID + " DESC", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                out.add(ModelInflater.inflateStoredNotification(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return out;
    }

    public Observable<List<StoredNotification>> getActiveObservable() {
        Observable<SqlBrite.Query> briteQuery = mBriteDb.createQuery(Database.TABLE_NOTIFICATIONS, "SELECT * FROM " + Database.TABLE_NOTIFICATIONS + " WHERE " + ACTIVE + " = 1 ORDER BY " + ID + " DESC");
        return briteQuery.map(query -> {
            Cursor cursor = query.run();
            List<StoredNotification> out = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    out.add(ModelInflater.inflateStoredNotification(cursor));
                } while (cursor.moveToNext());
                cursor.close();
            }
            return out;
        });
    }

    public void dismissAll() {
        ContentValues cv = new ContentValues();
        cv.put(ACTIVE, 0);
        mBriteDb.update(Database.TABLE_NOTIFICATIONS, cv, ACTIVE + " = 1");
    }

    private void delete(int id) {
        mBriteDb.delete(Database.TABLE_NOTIFICATIONS, ID + " = ?", String.valueOf(id));
    }

    // Only allow 50 notifications to be stored
    public void prune() {
        BriteDatabase.Transaction transaction = mBriteDb.newTransaction();
        try {
            String sql = SQLiteQueryBuilder.buildQueryString(
                    false,
                    Database.TABLE_NOTIFICATIONS,
                    new String[]{ID},
                    "",
                    null,
                    null,
                    ID + " ASC",
                    null);
            Cursor cursor = mBriteDb.query(sql);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    for (int i = cursor.getCount(); i > 50; i--) {
                        delete(cursor.getInt(cursor.getColumnIndex(ID)));
                        if (!cursor.moveToNext()) {
                            break;
                        }
                    }
                }
                cursor.close();
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }
}
