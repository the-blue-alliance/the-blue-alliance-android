package com.thebluealliance.androidclient.database.tables;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.models.Favorite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class FavoritesTable {
    public static final String KEY = "key",
            USER_NAME = "userName",
            MODEL_KEY = "modelKey",
            MODEL_ENUM = "model_enum";

    private SQLiteDatabase mDb;

    public FavoritesTable(SQLiteDatabase db) {
        this.mDb = db;
    }

    public long add(Favorite in) {
        if (!exists(in.getKey())) {
            return mDb.insert(Database.TABLE_FAVORITES, null, in.getParams());
        }
        return -1;
    }

    public void add(List<Favorite> in) {
        mDb.beginTransaction();
        try {
            for (Favorite favorite : in) {
                if (!unsafeExists(favorite.getKey())) {
                    mDb.insert(Database.TABLE_FAVORITES, null, favorite.getParams());
                }
            }
            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
    }

    public void remove(String key) {
        mDb.delete(Database.TABLE_FAVORITES, KEY + " = ?", new String[]{key});
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

    public boolean unsafeExists(String key) {
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

    public ArrayList<Favorite> getForUser(String user) {
        Cursor cursor = mDb.query(Database.TABLE_FAVORITES, null, USER_NAME + " = ?", new String[]{user}, null, null, MODEL_ENUM + " ASC", null);
        ArrayList<Favorite> favorites = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                favorites.add(ModelInflater.inflateFavorite(cursor));
            } while (cursor.moveToNext());
        }
        return favorites;
    }

    public void recreate(String user) {
        mDb.delete(Database.TABLE_FAVORITES, USER_NAME + " = ?", new String[]{user});
    }
}
