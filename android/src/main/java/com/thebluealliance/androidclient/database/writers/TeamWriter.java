package com.thebluealliance.androidclient.database.writers;


import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Team;

import android.support.annotation.WorkerThread;

import javax.inject.Inject;

public class TeamWriter extends BaseDbWriter<Team> {
    @Inject
    public TeamWriter(Database db, BriteDatabase briteDb) {
        super(db, briteDb);
    }

    @Override
    @WorkerThread
    public void write(Team team) {
        mDb.getTeamsTable().add(team);
    }
}