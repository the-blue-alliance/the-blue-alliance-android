package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Match;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MatchListWriter implements Action1<List<Match>> {
    private Database mDb;

    @Inject
    public MatchListWriter(Database db) {
        mDb = db;
    }

    @Override
    public void call(List<Match> matches) {
        Schedulers.io().createWorker().schedule(() -> mDb.getMatchesTable().add(matches));
    }
}