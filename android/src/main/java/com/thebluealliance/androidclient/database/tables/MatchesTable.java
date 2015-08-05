package com.thebluealliance.androidclient.database.tables;

import android.database.Cursor;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.models.Match;

import java.util.ArrayList;
import java.util.List;

public class MatchesTable extends ModelTable<Match> {
    public static final String KEY = "key",
            MATCHNUM = "matchNumber",
            SETNUM = "setNumber",
            EVENT = "eventKey",
            TIMESTRING = "timeString",
            TIME = "time",
            ALLIANCES = "alliances",
            VIDEOS = "videos";

    private Database mDb;

    public MatchesTable(Database mDb){
        super(mDb);
        this.mDb = mDb;
    }

    @Override
    protected String getTableName() {
        return Database.TABLE_MATCHES;
    }

    @Override
    protected String getKeyColumn() {
        return KEY;
    }

    @Override
    public Match inflate(Cursor cursor) {
        return ModelInflater.inflateMatch(cursor);
    }

    public List<Match> getTeamAtEventMatches(String teamKey, String eventKey) {
        Cursor cursor = mDb.rawQuery("SELECT * FROM `" + Database.TABLE_MATCHES + "` WHERE `" + EVENT
          + "` = ? AND `" + ALLIANCES + "` LIKE '%" + teamKey + "," + "%'", new String[]{eventKey});
        List<Match> models = new ArrayList<>(cursor == null ? 0 : cursor.getCount());
        if (cursor == null || !cursor.moveToFirst()) {
            return models;
        }
        do {
            models.add(inflate(cursor));
        } while (cursor.moveToNext());
        return models;
    }
}
