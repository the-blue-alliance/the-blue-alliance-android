package com.thebluealliance.androidclient.database.writers;

import com.google.common.collect.ImmutableList;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Team;

import android.support.annotation.WorkerThread;

import java.util.List;

import javax.inject.Inject;


public class TeamListWriter extends BaseDbWriter<List<Team>> {
    @Inject
    public TeamListWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(List<Team> teams) {
        mDb.getTeamsTable().add(ImmutableList.copyOf(teams));
    }
}