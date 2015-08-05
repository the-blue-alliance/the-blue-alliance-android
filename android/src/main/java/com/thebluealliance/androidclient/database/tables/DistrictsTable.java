package com.thebluealliance.androidclient.database.tables;

import android.database.Cursor;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.models.District;

public class DistrictsTable extends ModelTable<District> {

    public static final String KEY = "key",
            ABBREV = "abbrev",
            ENUM = "enum",
            YEAR = "year",
            NAME = "name";

    private Database mDb;

    public DistrictsTable(Database mDb){
        super(mDb);
        this.mDb = mDb;
    }

    @Override
    protected String getTableName() {
        return Database.TABLE_DISTRICTS;
    }

    @Override
    protected String getKeyColumn() {
        return KEY;
    }

    @Override
    public District inflate(Cursor cursor) {
        return ModelInflater.inflateDistrict(cursor);
    }
}
