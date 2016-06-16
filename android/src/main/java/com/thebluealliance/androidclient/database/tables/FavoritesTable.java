package com.thebluealliance.androidclient.database.tables;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.models.Favorite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class FavoritesTable {
    public static final String KEY = "key",
            USER_NAME = "userName",
            MODEL_KEY = "modelKey",
            MODEL_ENUM = "model_enum";

    private SQLiteDatabase mDb;
    private BriteDatabase mBriteDb;

    public FavoritesTable(SQLiteDatabase db, BriteDatabase briteDb) {
        mDb = db;
        mBriteDb = briteDb;
    }

    public long add(Favorite in) {
        if (!exists(in.getKey())) {
            return mBriteDb.insert(Database.TABLE_FAVORITES, in.getParams());
        }
        return -1;
    }

    public void add(List<Favorite> in) {
        BriteDatabase.Transaction transaction = mBriteDb.newTransaction();
        try {
            for (Favorite favorite : in) {
                add(favorite);
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    public void remove(String key) {
        mBriteDb.delete(Database.TABLE_FAVORITES, KEY + " = ?", key);
    }

    public boolean exists(String key) {
        Cursor cursor = mDb.query(Database.TABLE_FAVORITES, null, KEY + " = ?", new String[]{key}, null, null, null, null);
        boolean result;
        if (cursor != null) {
            result = cursor.moveToFirst();
            cursor.close();
        } else {
            result = false;
        }
        return result;
    }

    public Favorite get(String key) {
        Cursor cursor = mDb.query(Database.TABLE_FAVORITES, null, KEY + " = ?", new String[]{key}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            return ModelInflater.inflateFavorite(cursor);
        }
        return null;
    }

    public Observable<Favorite> getObservable(String key) {
        String sql = SQLiteQueryBuilder.buildQueryString(
                true,
                Database.TABLE_FAVORITES,
                null,
                KEY + " = ?",
                null,
                null,
                null,
                null);
        return mBriteDb.createQuery(Database.TABLE_FAVORITES, sql, key).map((query) -> {
            Cursor cursor = query.run();
            if (cursor != null && cursor.moveToFirst()) {
                return ModelInflater.inflateFavorite(cursor);
            }
            return null;
        });
    }

    public List<Favorite> getForUser(String user) {
        Cursor cursor = mDb.query(Database.TABLE_FAVORITES, null, USER_NAME + " = ?", new String[]{user}, null, null, MODEL_ENUM + " ASC", null);
        List<Favorite> favorites = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                favorites.add(ModelInflater.inflateFavorite(cursor));
            } while (cursor.moveToNext());
        }
        return favorites;
    }

    public Observable<List<Favorite>> getObservableForUser(String user) {
        String sql = SQLiteQueryBuilder.buildQueryString(
                true,
                Database.TABLE_FAVORITES,
                null,
                USER_NAME + " = ?",
                null,
                null,
                MODEL_ENUM + " ASC",
                null);
        return mBriteDb.createQuery(Database.TABLE_FAVORITES, sql, user).map((query) -> {
            Cursor cursor = query.run();
            List<Favorite> favorites = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    favorites.add(ModelInflater.inflateFavorite(cursor));
                } while (cursor.moveToNext());
            }
            return favorites;
        });
    }

    public void recreate(String user) {
        mBriteDb.delete(Database.TABLE_FAVORITES, USER_NAME + " = ?", user);
    }
}
