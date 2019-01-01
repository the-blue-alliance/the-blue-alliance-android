package com.thebluealliance.androidclient.database.writers;

import android.support.annotation.WorkerThread;

import com.google.common.collect.ImmutableList;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.DistrictRanking;

import java.util.List;

import javax.inject.Inject;

public class DistrictTeamListWriter extends BaseDbWriter<List<DistrictRanking>> {
    @Inject
    public DistrictTeamListWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(List<DistrictRanking> districtTeams, Long lastModified) {
        mDb.getDistrictTeamsTable().add(ImmutableList.copyOf(districtTeams), lastModified);
    }
}