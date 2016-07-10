package com.thebluealliance.androidclient.database.tables;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.models.Subscription;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class SubscriptionsTable {
    public static final String KEY = "key",
            USER_NAME = "userName",
            MODEL_KEY = "modelKey",
            MODEL_ENUM = "model_enum",
            NOTIFICATION_SETTINGS = "settings";

    private SQLiteDatabase mDb;
    private BriteDatabase mBriteDb;

    public SubscriptionsTable(SQLiteDatabase db, BriteDatabase briteDb) {
        mDb = db;
        mBriteDb = briteDb;
    }

    public long add(Subscription in) {
        if (!exists(in.getKey())) {
            return mBriteDb.insert(Database.TABLE_SUBSCRIPTIONS, in.getParams());
        } else {
            return update(in.getKey(), in);
        }
    }

    public int update(String key, Subscription in) {
        return mBriteDb.update(Database.TABLE_SUBSCRIPTIONS, in.getParams(), KEY + " = ?", key);
    }

    public void add(List<Subscription> in) {
        BriteDatabase.Transaction transaction = mBriteDb.newTransaction();
        try {
            for (Subscription subscription : in) {
                add(subscription);
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
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

    public void remove(String key) {
        mBriteDb.delete(Database.TABLE_SUBSCRIPTIONS, KEY + " = ?", key);
    }

    public Subscription get(String key) {
        Cursor cursor = mDb.query(Database.TABLE_SUBSCRIPTIONS, null, KEY + " = ?", new String[]{key}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            return ModelInflater.inflateSubscription(cursor);
        }
        return null;
    }

    public Observable<Subscription> getObservable(String key) {
        String sql = SQLiteQueryBuilder.buildQueryString(
                true,
                Database.TABLE_SUBSCRIPTIONS,
                null,
                KEY + " = ?",
                null,
                null,
                null,
                null);
        Observable<SqlBrite.Query> briteQuery = mBriteDb.createQuery(Database.TABLE_SUBSCRIPTIONS, sql, key);
        return briteQuery.map(query -> {
            Cursor cursor = query.run();
            if (cursor != null && cursor.moveToFirst()) {
                return ModelInflater.inflateSubscription(cursor);
            }
            return null;
        });
    }

    public List<Subscription> getForUser(String user) {
        Cursor cursor = mDb.query(Database.TABLE_SUBSCRIPTIONS, null, USER_NAME + " = ?", new String[]{user}, null, null, MODEL_ENUM + " ASC", null);
        List<Subscription> subscriptions = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                subscriptions.add(ModelInflater.inflateSubscription(cursor));
            } while (cursor.moveToNext());
        }
        return subscriptions;
    }

    public Observable<List<Subscription>> getObservableForUser(String user) {
        String sql = SQLiteQueryBuilder.buildQueryString(
                true,
                Database.TABLE_SUBSCRIPTIONS,
                null,
                USER_NAME + " = ?",
                null,
                null,
                MODEL_ENUM + " ASC",
                null);
        return mBriteDb.createQuery(Database.TABLE_SUBSCRIPTIONS, sql, user).map((query) -> {
            Cursor cursor = query.run();
            List<Subscription> subscriptions = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    subscriptions.add(ModelInflater.inflateSubscription(cursor));
                } while (cursor.moveToNext());
            }
            return subscriptions;
        });
    }

    public void recreate(String user) {
        mBriteDb.delete(Database.TABLE_SUBSCRIPTIONS, USER_NAME + " = ?", user);
    }

    public boolean hasNotificationType(String key, String notificationType) {
        if (!exists(key)) {
            return false;
        }
        String settings = get(key).getNotificationSettings();
        return settings.contains(notificationType);
    }
}
