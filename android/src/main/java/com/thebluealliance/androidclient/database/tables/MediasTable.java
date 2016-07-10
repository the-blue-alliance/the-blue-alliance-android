package com.thebluealliance.androidclient.database.tables;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.models.Media;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MediasTable extends ModelTable<Media> {
    public static final String TYPE = "type",
            FOREIGNKEY = "foreignKey",
            TEAMKEY = "teamKey",
            DETAILS = "details",
            YEAR = "year";

    public MediasTable(SQLiteDatabase db, BriteDatabase briteDb) {
        super(db, briteDb);
    }

    @Override
    public String getTableName() {
        return Database.TABLE_MEDIAS;
    }

    @Override
    public String getKeyColumn() {
        return FOREIGNKEY;
    }

    @Override
    public Media inflate(Cursor cursor) {
        return ModelInflater.inflateMedia(cursor);
    }
}
