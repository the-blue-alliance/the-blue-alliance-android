package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.DistrictTeam;

import android.support.annotation.WorkerThread;

import javax.inject.Inject;

public class DistrictTeamWriter extends BaseDbWriter<DistrictTeam> {
    @Inject
    public DistrictTeamWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(DistrictTeam districtTeam) {
        mDb.getDistrictTeamsTable().add(districtTeam);
    }
}