package com.thebluealliance.androidclient.database.writers;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Team;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.thebluealliance.androidclient.database.writers.YearsParticipatedWriter.YearsParticipatedInfo;

public class YearsParticipatedWriter implements Action1<YearsParticipatedInfo> {

    private final Database mDb;

    @Inject
    public YearsParticipatedWriter(Database db) {
        mDb = db;
    }

    @Override
    public void call(YearsParticipatedInfo yearsParticipatedInfo) {
        Schedulers.io().createWorker().schedule(() -> {
            Team team = mDb.getTeamsTable().get(yearsParticipatedInfo.teamKey);
            if (team != null) {
                team.setYearsParticipated(yearsParticipatedInfo.yearsParticipated);
                mDb.getTeamsTable().add(team);
            }
        });
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
