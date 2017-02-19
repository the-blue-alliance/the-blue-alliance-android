package com.thebluealliance.androidclient.database.tables;

import com.google.gson.Gson;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.models.District;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DistrictsTable extends ModelTable<District> {

    public static final String KEY = "key",
            ABBREV = "abbrev",
            ENUM = "enum",
            YEAR = "year",
            NAME = "name",
            LAST_MODIFIED = "last_modified";

    public DistrictsTable(SQLiteDatabase db, Gson gson){
        super(db, gson);
    }

    @Override
    public String getTableName() {
        return Database.TABLE_DISTRICTS;
    }

    @Override
    public String getKeyColumn() {
        return KEY;
    }

    @Override
    public String getLastModifiedColumn() {
        return LAST_MODIFIED;
    }

    @Override
    public District inflate(Cursor cursor) {
        return ModelInflater.inflateDistrict(cursor);
    }
}
