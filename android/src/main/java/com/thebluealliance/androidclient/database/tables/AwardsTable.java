package com.thebluealliance.androidclient.database.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.models.Award;

import java.util.ArrayList;
import java.util.List;

public class AwardsTable extends ModelTable<Award> {
    public static final String KEY = "key",
            ENUM = "enum",
            EVENTKEY = "eventKey",
            NAME = "name",
            YEAR = "year",
            WINNERS = "winners",
            LAST_MODIFIED = "last_modified";

    public AwardsTable(SQLiteDatabase db, Gson gson){
        super(db, gson);
    }

    @Override
    public String getTableName() {
        return Database.TABLE_AWARDS;
    }

    /*
     * For Awards, key is eventKey:awardName
     */
    @Override
    public String getKeyColumn() {
        return KEY;
    }

    @Override
    public String getLastModifiedColumn() {
        return LAST_MODIFIED;
    }

    @Override
    public Award inflate(Cursor cursor) {
        return ModelInflater.inflateAward(cursor, mGson);
    }

    public List<Award> getTeamAtEventAwards(String teamNumber, String eventKey) {
        Cursor cursor = mDb.rawQuery("SELECT * FROM `" + Database.TABLE_AWARDS + "` WHERE `" + EVENTKEY
          + "` = ? AND `" + WINNERS + "` LIKE '%\"team_key\":\"frc" + teamNumber + "\"%'", new String[]{eventKey});
        List<Award> models = new ArrayList<>(cursor == null ? 0 : cursor.getCount());
        if (cursor == null || !cursor.moveToFirst()) {
            return models;
        }
        do {
            models.add(inflate(cursor));
        } while (cursor.moveToNext());
        return models;
    }
}
