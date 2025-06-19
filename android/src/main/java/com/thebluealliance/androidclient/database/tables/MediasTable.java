package com.thebluealliance.androidclient.database.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.models.Media;

public class MediasTable extends ModelTable<Media> {
    public static final String TYPE = "type",
            FOREIGNKEY = "foreignKey",
            TEAMKEY = "teamKey",
            DETAILS = "details",
            B64_IMAGE = "b64_image",
            YEAR = "year";

    public MediasTable(SQLiteDatabase db, Gson gson){
        super(db, gson);
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
