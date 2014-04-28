package com.thebluealliance.androidclient.datafeed;

import com.thebluealliance.androidclient.models.BasicModel;

import java.util.ArrayList;

/**
 * File created by phil on 4/28/14.
 */
public interface DatabaseTable<T extends BasicModel> {
    /**
     * Adds something to the database
     * @param in Model to be added to the database
     * @return Sucess code from <a href="http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html#insert(java.lang.String, java.lang.String, android.content.ContentValues)">insert method</a>
     */
    public long add(T in);

    /**
     * Gets the model associated with given key
     * @param key Key to fetch from table
     * @return Associated model
     */
    public T get(String key);

    /**
     * Gets all records from table
     * @return ArrayList of all records
     */
    public ArrayList<T> getAll();

    /**
     * Does the given key (and the associated model) exist?
     * @param key Key to check
     * @return Existence status (sorry kids, only true or false. No existential crises here)
     */
    public boolean exists(String key);

    /**
     * Updates the given model in the database.
     * Model is found by key (so that obviously can't be changed, but all else can) and then updated
     * @param in Model to be updated
     * @return Sucess code from <a href="http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html#insert(java.lang.String, java.lang.String, android.content.ContentValues)">update method</a>, or -1 if nonexistent
     */
    public int update(T in);

    /**
     * Deletes model associated with given key from database
     * @param key Key to delete
     * @return Number of rows affected, as per <a href="http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html#delete(java.lang.String, java.lang.String, java.lang.String[])">teh docs</a>
     */
    public int delete(String key);
}
