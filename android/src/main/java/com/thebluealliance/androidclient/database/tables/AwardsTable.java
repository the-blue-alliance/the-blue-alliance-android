package com.thebluealliance.androidclient.database.tables;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.models.Award;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class AwardsTable extends ModelTable<Award> {
    public static final String KEY = "key",
            ENUM = "enum",
            EVENTKEY = "eventKey",
            NAME = "name",
            YEAR = "year",
            WINNERS = "winners";

    public AwardsTable(SQLiteDatabase db, BriteDatabase briteDb) {
        super(db, briteDb);
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
    public Award inflate(Cursor cursor) {
        return ModelInflater.inflateAward(cursor);
    }

    public List<Award> getTeamAtEventAwards(String teamKey, String eventKey) {
        Cursor cursor = mDb.rawQuery("SELECT * FROM `" + Database.TABLE_AWARDS + "` WHERE `" + EVENTKEY
          + "` = ? AND `" + WINNERS + "` LIKE '%\"" + teamKey + "\"%'", new String[]{eventKey});
        List<Award> models = new ArrayList<>(cursor == null ? 0 : cursor.getCount());
        if (cursor == null || !cursor.moveToFirst()) {
            return models;
        }
        do {
            models.add(inflate(cursor));
        } while (cursor.moveToNext());
        return models;
    }

    public Observable<List<Award>> getTeamAtEventAwardsObservable(String teamKey, String eventKey) {
        Observable<SqlBrite.Query> briteQuery = mBriteDb.createQuery(getTableName(), "SELECT * FROM `" + Database.TABLE_AWARDS + "` WHERE `" + EVENTKEY
                + "` = ? AND `" + WINNERS + "` LIKE '%\"" + teamKey + "\"%'", eventKey);
        return briteQuery.map(query -> {
            Cursor cursor = query.run();
            List<Award> models = new ArrayList<>(cursor == null ? 0 : cursor.getCount());
            if (cursor == null || !cursor.moveToFirst()) {
                return models;
            }
            do {
                models.add(inflate(cursor));
            } while (cursor.moveToNext());
            return models;
        });
    }
}
