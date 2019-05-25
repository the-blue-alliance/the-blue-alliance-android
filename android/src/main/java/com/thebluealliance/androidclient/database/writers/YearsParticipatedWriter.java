package com.thebluealliance.androidclient.database.writers;

import androidx.annotation.WorkerThread;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import javax.inject.Inject;

import static com.thebluealliance.androidclient.database.writers.YearsParticipatedWriter.YearsParticipatedInfo;

public class YearsParticipatedWriter extends BaseDbWriter<YearsParticipatedInfo> {

    private final TeamWriter mTeamWriter;

    @Inject
    public YearsParticipatedWriter(Database db, TeamWriter teamWriter) {
        super(db);
        mTeamWriter = teamWriter;
    }

    @Override
    @WorkerThread
    public void write(YearsParticipatedInfo yearsParticipatedInfo, Long lastModified) {
        Team team = mDb.getTeamsTable().get(yearsParticipatedInfo.teamKey);
        if (team != null && yearsParticipatedInfo.yearsParticipated != null) {
            team.setYearsParticipated(yearsParticipatedInfo.yearsParticipated);

            // Since we're taking years participated on to the team model, we don't actually want
            // to update the last-modified timestamp here and possibly prevent future writes of
            // actual team data
            mTeamWriter.write(team, team.getLastModified());
        }
    }


    public static class YearsParticipatedInfo {
        public final String teamKey;
        public final List<Integer> yearsParticipated;

        public YearsParticipatedInfo(String teamKey, List<Integer> yearsParticipated) {
            this.teamKey = teamKey;
            this.yearsParticipated = yearsParticipated;
        }
    }
}
