package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Match;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MatchWriter implements Action1<Match> {
    private Database mDb;

    @Inject
    public MatchWriter(Database db) {
        mDb = db;
    }

    @Override
    public void call(Match match) {
        Schedulers.io().createWorker().schedule(() -> mDb.getMatchesTable().add(match));
    }
}