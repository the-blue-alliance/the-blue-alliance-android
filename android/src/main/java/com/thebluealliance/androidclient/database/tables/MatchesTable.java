package com.thebluealliance.androidclient.database.tables;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.models.Match;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class MatchesTable extends ModelTable<Match> {
    public static final String KEY = "key",
            MATCHNUM = "matchNumber",
            SETNUM = "setNumber",
            EVENT = "eventKey",
            TIMESTRING = "timeString",
            TIME = "time",
            ALLIANCES = "alliances",
            VIDEOS = "videos",
            BREAKDOWN = "breakdown";

    public MatchesTable(SQLiteDatabase db, BriteDatabase briteDb) {
        super(db, briteDb);
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
        return ModelInflater.inflateMatch(cursor);
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

    public Observable<List<Match>> getTeamAtEventMatchesObservable(String teamKey, String eventKey) {
        String sql = String.format("SELECT * FROM %1$s WHERE %2$s = ? AND %3$s LIKE '%%\"%4$s\"%%'",
                Database.TABLE_MATCHES,
                EVENT,
                ALLIANCES,
                teamKey);
        Observable<SqlBrite.Query> briteQuery = mBriteDb.createQuery(getTableName(), sql, eventKey);
        return briteQuery.map(query -> {
            Cursor cursor = query.run();
            List<Match> models = new ArrayList<>(cursor == null ? 0 : cursor.getCount());
            if (cursor == null || !cursor.moveToFirst()) {
                return models;
            }
            do {
                models.add(inflate(cursor));
            } while (cursor.moveToNext());
            return models;
        });
    }

    public Observable<List<Match>> getEventMatchesObservable(String eventKey) {
        String sql = SQLiteQueryBuilder.buildQueryString(
                true,
                getTableName(),
                null,
                EVENT + " = ?",
                null,
                null,
                null,
                null);
        return mBriteDb.createQuery(getTableName(), sql, eventKey).map((query) -> {
            Cursor cursor = query.run();
            List<Match> matches = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    matches.add(inflate(cursor));
                } while (cursor.moveToNext());
                cursor.close();
                return matches;
            } else {
                return null;
            }
        });
    }
}
