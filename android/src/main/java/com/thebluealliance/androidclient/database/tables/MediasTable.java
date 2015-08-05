package com.thebluealliance.androidclient.database.tables;

import android.database.Cursor;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.models.Media;

public class MediasTable extends ModelTable<Media> {
    public static final String TYPE = "type",
            FOREIGNKEY = "foreignKey",
            TEAMKEY = "teamKey",
            DETAILS = "details",
            YEAR = "year";

    private Database mDb;

    public MediasTable(Database mDb){
        super(mDb);
        this.mDb = mDb;
    }

    @Override
    protected String getTableName() {
        return Database.TABLE_MEDIAS;
    }

    @Override
    protected String getKeyColumn() {
        return FOREIGNKEY;
    }

    @Override
    public Media inflate(Cursor cursor) {
        return ModelInflater.inflateMedia(cursor);
    }
}
