package com.thebluealliance.androidclient.database.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.models.Subscription;

import java.util.ArrayList;

public class SubscriptionsTable {
    public static final String KEY = "key",
            USER_NAME = "userName",
            MODEL_KEY = "modelKey",
            MODEL_ENUM = "model_enum",
            NOTIFICATION_SETTINGS = "settings";

    private SQLiteDatabase mDb;

    public SubscriptionsTable(SQLiteDatabase db) {
        this.mDb = db;
    }

    public long add(Subscription in) {
        if (!exists(in.getKey())) {
            return mDb.insert(Database.TABLE_SUBSCRIPTIONS, null, in.getParams());
        } else {
            return update(in.getKey(), in);
        }
    }

    public int update(String key, Subscription in) {
        return mDb.update(Database.TABLE_SUBSCRIPTIONS, in.getParams(), KEY + " = ?", new String[]{key});
    }

    public void add(ArrayList<Subscription> in) {
        mDb.beginTransaction();
        try {
            for (Subscription subscription : in) {
                if (!unsafeExists(subscription.getKey())) {
                    mDb.insert(Database.TABLE_SUBSCRIPTIONS, null, subscription.getParams());
                }
            }
            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
    }

    public boolean exists(String key) {
        Cursor cursor = mDb.query(Database.TABLE_SUBSCRIPTIONS, null, KEY + " = ?", new String[]{key}, null, null, null, null);
        boolean result;
        if (cursor != null) {
            result = cursor.moveToFirst();
            cursor.close();
        } else {
            result = false;
        }
        return result;
    }

    public boolean unsafeExists(String key) {
        Cursor cursor = mDb.query(Database.TABLE_SUBSCRIPTIONS, null, KEY + " = ?", new String[]{key}, null, null, null, null);
        boolean result;
        if (cursor != null) {
            result = cursor.moveToFirst();
            cursor.close();
        } else {
            result = false;
        }
        return result;
    }

    public void remove(String key) {
        mDb.delete(Database.TABLE_SUBSCRIPTIONS, KEY + " = ?", new String[]{key});
    }

    public Subscription get(String key) {
        Cursor cursor = mDb.query(Database.TABLE_SUBSCRIPTIONS, null, KEY + " = ?", new String[]{key}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            return ModelInflater.inflateSubscription(cursor);
        }
        return null;
    }

    public ArrayList<Subscription> getForUser(String user) {
        Cursor cursor = mDb.query(Database.TABLE_SUBSCRIPTIONS, null, USER_NAME + " = ?", new String[]{user}, null, null, MODEL_ENUM + " ASC", null);
        ArrayList<Subscription> subscriptions = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                subscriptions.add(ModelInflater.inflateSubscription(cursor));
            } while (cursor.moveToNext());
        }
        return subscriptions;
    }

    public void recreate(String user) {
        mDb.delete(Database.TABLE_SUBSCRIPTIONS, USER_NAME + " = ?", new String[]{user});
    }
}
