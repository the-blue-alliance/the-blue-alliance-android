package com.thebluealliance.androidclient.database.writers;

import androidx.annotation.WorkerThread;

import com.google.common.collect.ImmutableList;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import javax.inject.Inject;


public class TeamListWriter extends BaseDbWriter<List<Team>> {
    @Inject
    public TeamListWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(List<Team> teams, Long lastModified) {
        mDb.getTeamsTable().add(ImmutableList.copyOf(teams), lastModified);
    }
}