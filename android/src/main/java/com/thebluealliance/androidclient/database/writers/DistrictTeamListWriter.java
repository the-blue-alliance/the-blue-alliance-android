package com.thebluealliance.androidclient.database.writers;

import com.google.common.collect.ImmutableList;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.DistrictTeam;

import android.support.annotation.WorkerThread;

import java.util.List;

import javax.inject.Inject;

public class DistrictTeamListWriter extends BaseDbWriter<List<DistrictTeam>> {
    @Inject
    public DistrictTeamListWriter(Database db, BriteDatabase briteDb) {
        super(db, briteDb);
    }
    @Override
    @WorkerThread
    public void write(List<DistrictTeam> districtTeams) {
        mDb.getDistrictTeamsTable().add(ImmutableList.copyOf(districtTeams));
    }
}