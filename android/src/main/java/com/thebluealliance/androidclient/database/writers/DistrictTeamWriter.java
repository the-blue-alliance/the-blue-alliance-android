package com.thebluealliance.androidclient.database.writers;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.DistrictTeam;

import android.support.annotation.WorkerThread;

import javax.inject.Inject;

public class DistrictTeamWriter extends BaseDbWriter<DistrictTeam> {
    @Inject
    public DistrictTeamWriter(Database db, BriteDatabase briteDb) {
        super(db, briteDb);
    }

    @Override
    @WorkerThread
    public void write(DistrictTeam districtTeam) {
        mDb.getDistrictTeamsTable().add(districtTeam);
    }
}