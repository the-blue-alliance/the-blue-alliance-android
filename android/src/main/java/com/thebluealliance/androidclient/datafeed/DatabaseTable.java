package com.thebluealliance.androidclient.datafeed;

import com.thebluealliance.androidclient.models.BasicModel;

/**
 * File created by phil on 4/28/14.
 */
public interface DatabaseTable<T extends BasicModel> {
    /**
     * Adds something to the database
     * @param in Model to be added to the database
     * @return Success code from <a href="http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html#insert(java.lang.String, java.lang.String, android.content.ContentValues)">insert method</a>
     */
    public long add(T in);

    /**
     * Gets the model associated with given key
     * @param key Key to fetch from table
     * @return Associated model
     */
    public T get(String key);

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
     * @return Success code from <a href="http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html#insert(java.lang.String, java.lang.String, android.content.ContentValues)">update method</a>, or -1 if nonexistent
     */
    public int update(T in);

}
