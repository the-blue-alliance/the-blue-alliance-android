package com.thebluealliance.androidclient.database.tables;

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
            NAME = "name";

    private SQLiteDatabase mDb;

    public DistrictsTable(SQLiteDatabase db){
        super(db);
        this.mDb = db;
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
    public District inflate(Cursor cursor) {
        return ModelInflater.inflateDistrict(cursor);
    }
}
