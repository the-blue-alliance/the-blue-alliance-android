package com.thebluealliance.androidclient.database.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
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
            TIME = "time",
            ALLIANCES = "alliances",
            WINNER = "winner",
            VIDEOS = "videos",
            BREAKDOWN = "breakdown";
    @Deprecated public static final String TIMESTRING = "timeString";

    public MatchesTable(SQLiteDatabase db, Gson gson){
        super(db, gson);
    }

    @Override
    public String getTableName() {
        return Database.TABLE_MATCHES;
    }

    @Override
    public String getKeyColumn() {
        return KEY;
    }

    @Override
    public Match inflate(Cursor cursor) {
        return ModelInflater.inflateMatch(cursor, mGson);
    }

    public List<Match> getTeamAtEventMatches(String teamKey, String eventKey) {
        Cursor cursor = mDb.rawQuery("SELECT * FROM `" + Database.TABLE_MATCHES + "` WHERE `" + EVENT
          + "` = ? AND `" + ALLIANCES + "` LIKE '%\"" + teamKey + "\"%'", new String[]{eventKey});
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
