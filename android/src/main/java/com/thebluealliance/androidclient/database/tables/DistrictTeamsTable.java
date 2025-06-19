package com.thebluealliance.androidclient.database.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.models.DistrictRanking;

public class DistrictTeamsTable extends ModelTable<DistrictRanking> {

    public static final String KEY = "key",
            TEAM_KEY = "teamKey",
            DISTRICT_KEY = "districtKey",
            RANK = "rank",
            EVENT1_KEY = "event1Key",
            EVENT1_POINTS = "event1Points",
            EVENT2_KEY = "event2Key",
            EVENT2_POINTS = "event2Points",
            CMP_KEY = "cmpKey",
            CMP_POINTS = "cmpPoints",
            ROOKIE_POINTS = "rookiePoints",
            TOTAL_POINTS = "totalPoints";
    @Deprecated public static final String
            JSON = "json",
            DISTRICT_ENUM = "districtEnum",
            YEAR = "year";

    public DistrictTeamsTable(SQLiteDatabase db, Gson gson){
        super(db, gson);
    }

    @Override
    public String getTableName() {
        return Database.TABLE_DISTRICTTEAMS;
    }

    @Override
    public String getKeyColumn() {
        return KEY;
    }

    @Override
    public DistrictRanking inflate(Cursor cursor) {
        return ModelInflater.inflateDistrictTeam(cursor, mGson);
    }
}
