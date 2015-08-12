package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.DistrictTeam;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DistrictTeamWriter implements Action1<DistrictTeam> {
    private Database mDb;

    @Inject
    public DistrictTeamWriter(Database db) {
        mDb = db;
    }

    @Override
    public void call(DistrictTeam districtTeam) {
        Schedulers.io().createWorker()
          .schedule(() -> mDb.getDistrictTeamsTable().add(districtTeam));
    }
}