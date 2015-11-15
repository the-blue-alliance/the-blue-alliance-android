package com.thebluealliance.androidclient.database.writers;

import android.support.annotation.WorkerThread;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Team;

import javax.inject.Inject;

import static com.thebluealliance.androidclient.database.writers.YearsParticipatedWriter.YearsParticipatedInfo;

public class YearsParticipatedWriter extends BaseDbWriter<YearsParticipatedInfo> {
    @Inject
    public YearsParticipatedWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(YearsParticipatedInfo yearsParticipatedInfo) {
        Team team = mDb.getTeamsTable().get(yearsParticipatedInfo.teamKey);
        if (team != null) {
            team.setYearsParticipated(yearsParticipatedInfo.yearsParticipated);
            mDb.getTeamsTable().add(team);
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
