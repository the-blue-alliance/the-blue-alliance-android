package com.thebluealliance.androidclient.database.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.models.DistrictTeam;

public class DistrictTeamsTable extends ModelTable<DistrictTeam> {

    public static final String KEY = "key",
            TEAM_KEY = "teamKey",
            DISTRICT_KEY = "districtKey",
            DISTRICT_ENUM = "districtEnum",
            YEAR = "year",
            RANK = "rank",
            EVENT1_KEY = "event1Key",
            EVENT1_POINTS = "event1Points",
            EVENT2_KEY = "event2Key",
            EVENT2_POINTS = "event2Points",
            CMP_KEY = "cmpKey",
            CMP_POINTS = "cmpPoints",
            ROOKIE_POINTS = "rookiePoints",
            TOTAL_POINTS = "totalPoints",
            JSON = "json";

    private SQLiteDatabase mDb;

    public DistrictTeamsTable(SQLiteDatabase db){
        super(db);
        this.mDb = db;
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
    public DistrictTeam inflate(Cursor cursor) {
        return ModelInflater.inflateDistrictTeam(cursor);
    }
}
