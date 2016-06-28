package com.thebluealliance.androidclient.database.writers;

import com.google.gson.JsonArray;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Team;

import android.support.annotation.WorkerThread;

import javax.inject.Inject;

import static com.thebluealliance.androidclient.database.writers.YearsParticipatedWriter.YearsParticipatedInfo;

public class YearsParticipatedWriter extends BaseDbWriter<YearsParticipatedInfo> {

    private final TeamWriter mTeamWriter;

    @Inject
    public YearsParticipatedWriter(Database db, BriteDatabase briteDb, TeamWriter teamWriter) {
        super(db, briteDb);
        mTeamWriter = teamWriter;
    }

    @Override
    @WorkerThread
    public void write(YearsParticipatedInfo yearsParticipatedInfo) {
        Team team = mDb.getTeamsTable().get(yearsParticipatedInfo.teamKey);
        if (team != null && yearsParticipatedInfo.yearsParticipated != null) {
            team.setYearsParticipated(yearsParticipatedInfo.yearsParticipated);
            mTeamWriter.write(team);
        }
    }


    public static class YearsParticipatedInfo {
        public final String teamKey;
        public final JsonArray yearsParticipated;

        public YearsParticipatedInfo(String teamKey, JsonArray yearsParticipated) {
            this.teamKey = teamKey;
            this.yearsParticipated = yearsParticipated;
        }
    }
}
