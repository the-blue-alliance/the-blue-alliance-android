package com.thebluealliance.androidclient.database.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.database.model.DistrictDbModel;

public class DistrictsTable extends ModelTable<DistrictDbModel> {

    public static final String KEY = "key",
            ABBREV = "abbrev",
            YEAR = "year",
            NAME = "name";

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
    public DistrictDbModel inflate(Cursor cursor) {
        return ModelInflater.inflateDistrict(cursor);
    }
}
