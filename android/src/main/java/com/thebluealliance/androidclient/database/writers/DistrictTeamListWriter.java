package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.DistrictTeam;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DistrictTeamListWriter implements Action1<List<DistrictTeam>> {
    private Database mDb;

    @Inject
    public DistrictTeamListWriter(Database db) {
        mDb = db;
    }

    @Override
    public void call(List<DistrictTeam> districtTeams) {
        Schedulers.io().createWorker()
          .schedule(() -> mDb.getDistrictTeamsTable().add(districtTeams));
    }
}