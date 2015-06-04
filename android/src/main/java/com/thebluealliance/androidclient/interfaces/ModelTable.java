package com.thebluealliance.androidclient.interfaces;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.thebluealliance.androidclient.models.BasicModel;

import java.util.ArrayList;
import java.util.List;

/**
 * File created by phil on 6/21/14.
 */
public abstract class ModelTable<T extends BasicModel> {

    private SQLiteDatabase mDb;

    public ModelTable(SQLiteDatabase db){
        mDb = db;
    }

    public long add(T in) {
        mDb.beginTransaction();
        long ret;
        try {
            if (!exists(in.getKey())) {
                ret = mDb.insert(getTableName(), null, in.getParams());
                if (ret != -1) {
                    insertCallback(in);
                }
            } else {
                ret = update(in);
            }
            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
        return ret;
    }

    public void add(List<T> inList){
        mDb.beginTransaction();
        for (T in: inList) {
            add(in);
        }
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
    }

    public int update(T in){
        int ret;
        mDb.beginTransaction();
        try {
            ret = mDb.update(getTableName(), in.getParams(), getKeyColumn() + "=?", new String[]{in.getKey()});
            if (ret > 0) {
                updateCallback(in);
            }
            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
        return ret;
    }

    public T get(String key) {
        return get(key, null);
    }

    public T get(String key, String[] fields){
        Cursor cursor = mDb.query(getTableName(), fields, getKeyColumn() + " = ?", new String[]{key}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            T model = inflate(cursor);
            cursor.close();
            return model;
        } else {
            return null;
        }
    }

    public Cursor query (String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit){
        return mDb.query(getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public List<T> getAll(){
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + getTableName(), new String[]{});
        List<T> ret = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                T model = inflate(cursor);
                ret.add(model);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return ret;
    }

    public boolean exists(String key){
        Cursor cursor = mDb.query(getTableName(), new String[]{getKeyColumn()}, getKeyColumn() + "=?", new String[]{key}, null, null, null, null);
        boolean result;
        if (cursor != null) {
            result = cursor.moveToFirst();
            cursor.close();
        } else {
            result = false;
        }
        return result;
    }

    public int delete(T in) {
        int ret;
        mDb.beginTransaction();
        try {
            ret = mDb.delete(getTableName(), getKeyColumn() + " = ?", new String[]{in.getKey()});
            if (ret > 0) {
                deleteCallback(in);
            }
            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
        return ret;
    }

    public int delete(String whereClause, String[] whereArgs) {
        return mDb.delete(getTableName(), whereClause, whereArgs);
    }

    protected void insertCallback(T model){
        // default to no op
    }

    protected void updateCallback(T model){
        // default to no op
    }

    protected void deleteCallback(T model){
        // default to no op
    }

    protected abstract String getTableName();
    protected abstract String getKeyColumn();
    public abstract T inflate(Cursor cursor);
}
