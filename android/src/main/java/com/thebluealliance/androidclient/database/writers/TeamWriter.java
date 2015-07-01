package com.thebluealliance.androidclient.database.writers;


import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Team;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class TeamWriter implements Action1<Team> {
    private Database mDb;

    @Inject
    public TeamWriter(Database db) {
        mDb = db;
    }

    @Override
    public void call(Team team) {
        Schedulers.io().createWorker().schedule(() -> mDb.getTeamsTable().add(team));
    }
}