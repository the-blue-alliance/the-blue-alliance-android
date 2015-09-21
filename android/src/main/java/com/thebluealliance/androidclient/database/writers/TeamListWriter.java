package com.thebluealliance.androidclient.database.writers;

import com.google.common.collect.ImmutableList;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class TeamListWriter implements Action1<List<Team>> {

    private Database mDb;

    @Inject
    public TeamListWriter(Database db) {
        mDb = db;
    }

    @Override public void call(List<Team> teams) {
        Schedulers.io().createWorker()
          .schedule(() -> mDb.getTeamsTable().add(ImmutableList.copyOf(teams)));
    }
}