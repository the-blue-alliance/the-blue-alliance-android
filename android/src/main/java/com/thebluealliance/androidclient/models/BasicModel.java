package com.thebluealliance.androidclient.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.listitems.ListElement;

import java.util.HashMap;
import java.util.concurrent.Semaphore;

/**
 * File created by phil on 4/28/14.
 */
public abstract class BasicModel<T extends BasicModel> {

    /* Map of the requested fields for this object
     * This is done for two reasons - since different parts of the model are loaded from different API queries,
     * we aren't necessarily going to have every bit of data for the model.
     * Also, we can now only request the parts of the model that we want to use
     */
    protected HashMap<String, Object> fields;

    //database table that holds this model's information
    private String table;

    public BasicModel(String table){
        this.table = table;
        fields = new HashMap<>();
    }

    public Cursor get(Context c, String[] fields, String whereClause, String[] whereVars){
        Semaphore dbSemaphore = null;
        Cursor cursor = null;
        try{
            dbSemaphore = Database.getSemaphore(c);
            dbSemaphore.acquire();
            cursor = Database.getInstance(c).getDb().query(table, fields, whereClause, whereVars, null, null, null);
        } catch (InterruptedException e) {
            Log.w("Fetch Model", "Unable to aquire database semaphore");
        }finally {
            if(dbSemaphore != null) {
                dbSemaphore.release();
            }
        }
        return cursor;
    }

    public abstract void addFields(String... fields);

    public abstract ListElement render();

    public abstract ContentValues getParams();

    /*
     * When we're ready for it, I can foresee wanting easy inflating/deflating with json. Uncomment whenever that is...
    public JsonObject toJson();
    public static BasicModel fromJson(Json Object in);
     */

}
