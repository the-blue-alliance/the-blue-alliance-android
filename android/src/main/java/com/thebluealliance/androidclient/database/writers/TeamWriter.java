package com.thebluealliance.androidclient.database.writers;


import androidx.annotation.WorkerThread;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Team;

import javax.inject.Inject;

public class TeamWriter extends BaseDbWriter<Team> {
    @Inject
    public TeamWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(Team team) {
        mDb.getTeamsTable().add(team);
    }
}