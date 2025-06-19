package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.DistrictRanking;

import javax.inject.Inject;

import androidx.annotation.WorkerThread;

public class DistrictTeamWriter extends BaseDbWriter<DistrictRanking> {
    @Inject
    public DistrictTeamWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(DistrictRanking districtTeam) {
        mDb.getDistrictTeamsTable().add(districtTeam);
    }
}